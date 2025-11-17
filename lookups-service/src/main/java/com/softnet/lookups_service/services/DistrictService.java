package com.softnet.lookups_service.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softnet.lookups_service.cache.CacheService;
import com.softnet.lookups_service.dto.DistrictDto;
import com.softnet.lookups_service.exceptions.ConflictException;
import com.softnet.lookups_service.exceptions.NotFoundException;
import com.softnet.lookups_service.mappers.DistrictMapper;
import com.softnet.lookups_service.model.District;
import com.softnet.lookups_service.model.Region;
import com.softnet.lookups_service.repository.DistrictRepository;
import com.softnet.lookups_service.repository.OutboxRepository;
import com.softnet.lookups_service.repository.RegionRepository;
import com.softnet.lookups_service.util.BaseService;

@Service
public class DistrictService extends BaseService<District, DistrictDto, UUID> {

    private final DistrictRepository districtRepository;
    private final RegionRepository regionRepository;
    private final DistrictMapper districtMapper;
    private final CacheService cacheService;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public DistrictService(DistrictRepository districtRepository,
            RegionRepository regionRepository,
            DistrictMapper districtMapper,
            CacheService cacheService,
            OutboxRepository outboxRepository,
            ObjectMapper objectMapper) {
        super(outboxRepository, objectMapper);
        this.districtRepository = districtRepository;
        this.regionRepository = regionRepository;
        this.districtMapper = districtMapper;
        this.cacheService = cacheService;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    protected JpaRepository<District, UUID> repository() {
        return districtRepository;
    }

    @Override
    protected DistrictDto toDto(District entity) {
        return districtMapper.toDto(entity);
    }

    @Override
    protected District toEntity(DistrictDto dto) {
        Region parent = regionRepository.findById(dto.getRegionId())
                .orElseThrow(() -> new NotFoundException("Parent region not found: " + dto.getRegionId()));
        District d = districtMapper.toEntity(dto, parent);
        if (d.getDistrictId() == null) {
            d.setDistrictId(UUID.randomUUID());
        }
        if (d.getCreatedDate() == null) {
            d.setCreatedDate(Instant.now());
        }
        return d;
    }

    @Override
    protected UUID getEntityId(District entity) {
        return entity.getDistrictId();
    }

    @Override
    protected String aggregateType() {
        return "DISTRICT";
    }

    @Override
    protected void applyUpdateToEntity(District entity, DistrictDto dto) {
        entity.setDistrictName(dto.getDistrictName());
        entity.setLastModifiedBy(dto.getLastModifiedBy());
        entity.setLastModifiedDate(Instant.now());
        // allow re-parenting region on update; validate parent exists
        if (dto.getRegionId() != null && (entity.getRegion() == null || !entity.getRegion().getRegionId().equals(dto.getRegionId()))) {
            Region parent = regionRepository.findById(dto.getRegionId())
                    .orElseThrow(() -> new NotFoundException("Parent region not found: " + dto.getRegionId()));
            entity.setRegion(parent);
        }
        entity.setActive(dto.getIsActive() == null ? entity.isActive() : dto.getIsActive());
    }

    public DistrictDto findByIdCached(UUID id) {
        String key = "district:" + id;
        return cacheReadSingle(key, id, District.class, DistrictDto.class);
    }

    private <E, D> D cacheReadSingle(String key, UUID id, Class<E> entityClass, Class<D> dtoClass) {
        try {
            var opt = cacheService.get(key);
            if (opt.isPresent()) {
                return objectMapper.readValue(opt.get(), objectMapper.getTypeFactory().constructType(dtoClass));
            }
        } catch (Exception ignored) {
        }
        District e = districtRepository.findById(id).orElseThrow(() -> new NotFoundException("District not found: " + id));
        DistrictDto dto = districtMapper.toDto(e);
        try {
            cacheService.put(key, objectMapper.writeValueAsString(dto), 3600);
        } catch (Exception ignored) {
        }
        return (D) dto;
    }

    public List<DistrictDto> listAllCached() {
        String key = "district:all";
        return cacheReadList(key, districtRepository.findAllByIsActiveTrue(), districtMapper::toDto, 300);
    }

    private <E, D> List<D> cacheReadList(String key, List<E> entities, java.util.function.Function<E, D> mapper, int ttl) {
        try {
            var opt = cacheService.get(key);
            if (opt.isPresent()) {
                return objectMapper.readValue(opt.get(), objectMapper.getTypeFactory().constructCollectionType(List.class, mapper.apply(entities.get(0)).getClass()));
            }
        } catch (Exception ignored) {
        }
        List<D> dtos = entities.stream().map(mapper).toList();
        try {
            cacheService.put(key, objectMapper.writeValueAsString(dtos), ttl);
        } catch (Exception ignored) {
        }
        return dtos;
    }

    @Override
    protected void evictCache(UUID id) {
        if (id != null) {
            cacheService.invalidate("district:" + id);
        }
        cacheService.invalidate("district:all");
        // if district changed, invalidate region-specific cache
        cacheService.invalidateByPrefix("district:region:");
    }

    @Override
    protected void validateBeforeCreate(DistrictDto dto) {
        if (districtRepository.existsByDistrictNameIgnoreCase(dto.getDistrictName())) {
            throw new ConflictException("District name already exists: " + dto.getDistrictName());
        }
        // ensure parent region exists
        if (!regionRepository.existsById(dto.getRegionId())) {
            throw new NotFoundException("Parent region not found: " + dto.getRegionId());
        }
    }

    @Override
    protected void validateBeforeUpdate(UUID id, DistrictDto dto) {
        districtRepository.findByDistrictNameIgnoreCase(dto.getDistrictName()).ifPresent(existing -> {
            if (!existing.getDistrictId().equals(id)) {
                throw new ConflictException("District name already used by another district.");
            }
        });

        if (dto.getRegionId() != null && !regionRepository.existsById(dto.getRegionId())) {
            throw new NotFoundException("Parent region not found: " + dto.getRegionId());
        }
    }

    @Override
    protected void setInactive(District entity) {
        entity.setActive(false);
        entity.setLastModifiedDate(Instant.now());
    }

    public List<DistrictDto> listByRegionCached(UUID regionId) {
        String key = "district:region:" + regionId;
        try {
            var opt = cacheService.get(key);
            if (opt.isPresent()) {
                return objectMapper.readValue(opt.get(), objectMapper.getTypeFactory().constructCollectionType(List.class, DistrictDto.class));
            }
        } catch (Exception ignored) {
        }
        var list = districtRepository.findByRegionRegionId(regionId).stream().map(districtMapper::toDto).toList();
        try {
            cacheService.put(key, objectMapper.writeValueAsString(list), 300);
        } catch (Exception ignored) {
        }
        return list;
    }
}
