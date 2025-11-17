package com.softnet.lookups_service.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softnet.lookups_service.cache.CacheService;
import com.softnet.lookups_service.dto.DistrictDto;
import com.softnet.lookups_service.dto.RegionDto;
import com.softnet.lookups_service.exceptions.ConflictException;
import com.softnet.lookups_service.exceptions.NotFoundException;
import com.softnet.lookups_service.mappers.DistrictMapper;
import com.softnet.lookups_service.mappers.RegionMapper;
import com.softnet.lookups_service.model.Region;
import com.softnet.lookups_service.repository.DistrictRepository;
import com.softnet.lookups_service.repository.OutboxRepository;
import com.softnet.lookups_service.repository.RegionRepository;
import com.softnet.lookups_service.util.BaseService;

@Service
public class RegionService extends BaseService<Region, RegionDto, UUID> {

    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final RegionMapper regionMapper;
    private final CacheService cacheService;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public RegionService(RegionRepository regionRepository,
                         DistrictRepository districtRepository,
                         RegionMapper regionMapper,
                         CacheService cacheService,
                         OutboxRepository outboxRepository,
                         ObjectMapper objectMapper) {
        super(outboxRepository, objectMapper);
        this.regionRepository = regionRepository;
        this.districtRepository = districtRepository;
        this.regionMapper = regionMapper;
        this.cacheService = cacheService;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    protected JpaRepository<Region, UUID> repository() { return regionRepository; }

    @Override
    protected RegionDto toDto(Region entity) { return regionMapper.toDto(entity); }

    @Override
    protected Region toEntity(RegionDto dto) {
        Region r = regionMapper.toEntity(dto);
        if (r.getRegionId() == null) r.setRegionId(UUID.randomUUID());
        if (r.getCreatedDate() == null) r.setCreatedDate(Instant.now());
        return r;
    }

    @Override
    protected UUID getEntityId(Region entity) { return entity.getRegionId(); }

    @Override
    protected String aggregateType() { return "REGION"; }

    @Override
    protected void applyUpdateToEntity(Region entity, RegionDto dto) {
        entity.setRegionName(dto.getRegionName());
        entity.setLastModifiedBy(dto.getLastModifiedBy());
        entity.setLastModifiedDate(Instant.now());
        entity.setActive(dto.getIsActive() == null ? entity.isActive() : dto.getIsActive());
    }

    @Override
    protected void evictCache(UUID id) {
        if (id != null) cacheService.invalidate("region:" + id);
        cacheService.invalidate("region:all");
        cacheService.invalidateByPrefix("district:region:" + (id != null ? id.toString() : ""));
    }

    @Override
    protected void validateBeforeCreate(RegionDto dto) {
        if (regionRepository.existsByRegionNameIgnoreCase(dto.getRegionName())) {
            throw new ConflictException("Region with same name exists: " + dto.getRegionName());
        }
    }

    @Override
    protected void validateBeforeUpdate(UUID id, RegionDto dto) {
        regionRepository.findByRegionNameIgnoreCase(dto.getRegionName())
                .ifPresent(existing -> { if (!existing.getRegionId().equals(id))
                    throw new ConflictException("Region name already used by another region.");
                });
    }

    @Override
    protected void setInactive(Region entity) {
        // validation: prevent deactivation if districts exist
        boolean hasChildren = districtRepository.existsByRegionRegionId(entity.getRegionId());
        if (hasChildren) {
            throw new ConflictException("Cannot deactivate region that has districts. Remove or move districts first.");
        }
        entity.setActive(false);
        entity.setLastModifiedDate(Instant.now());
    }

    // Additional read helpers
    public RegionDto findByIdCached(UUID id) {
        String key = "region:" + id;
        return cacheReadSingle(key, id, Region.class, RegionDto.class);
    }

    public List<RegionDto> listAllCached() {
        String key = "region:all";
        return cacheReadList(key, regionRepository.findAllByIsActiveTrue(), regionMapper::toDto, 300);
    }

    // Hierarchy helper: get districts by region id (service interacts with DistrictRepository)
    public List<DistrictDto> getDistrictsByRegion(UUID regionId, DistrictMapper districtMapper) {
        var districts = districtRepository.findByRegionRegionId(regionId);
        return districts.stream().map(districtMapper::toDto).toList();
    }

    // Utility methods to reuse caching read patterns (we'll implement these in BaseCrudService or here)
    // For brevity, below are simple implementations (but you can factor to BaseCrudService).
    private <E,D> D cacheReadSingle(String key, UUID id, Class<E> entityClass, Class<D> dtoClass) {
        try {
            var opt = cacheService.get(key);
            if (opt.isPresent()) {
                return objectMapper.readValue(opt.get(), objectMapper.getTypeFactory().constructType(dtoClass));
            }
        } catch (Exception ignored) { }
        Region e = regionRepository.findById(id).orElseThrow(() -> new NotFoundException("Region not found: " + id));
        RegionDto dto = regionMapper.toDto(e);
        try { cacheService.put(key, objectMapper.writeValueAsString(dto), 3600); } catch (Exception ignored) {}
        return (D) dto;
    }

    private <E,D> List<D> cacheReadList(String key, List<E> entities, java.util.function.Function<E,D> mapper, int ttl) {
        try {
            var opt = cacheService.get(key);
            if (opt.isPresent()) {
                return objectMapper.readValue(opt.get(), objectMapper.getTypeFactory().constructCollectionType(List.class, mapper.apply(entities.get(0)).getClass()));
            }
        } catch (Exception ignored) {}
        List<D> dtos = entities.stream().map(mapper).toList();
        try { cacheService.put(key, objectMapper.writeValueAsString(dtos), ttl); } catch (Exception ignored) {}
        return dtos;
    }
}
