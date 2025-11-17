package com.softnet.lookups_service.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softnet.lookups_service.cache.CacheService;
import com.softnet.lookups_service.dto.CurrencyDto;
import com.softnet.lookups_service.exceptions.ConflictException;
import com.softnet.lookups_service.exceptions.NotFoundException;
import com.softnet.lookups_service.mappers.CurrencyMapper;
import com.softnet.lookups_service.model.Currency;
import com.softnet.lookups_service.repository.CurrencyRepository;
import com.softnet.lookups_service.repository.OutboxRepository;
import com.softnet.lookups_service.util.BaseService;

@Service
public class CurrencyService extends BaseService<Currency, CurrencyDto, UUID> {

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;
    private final CacheService cacheService;
    private final ObjectMapper objectMapper;

    public CurrencyService(CurrencyRepository currencyRepository,
                           CurrencyMapper currencyMapper,
                           CacheService cacheService,
                           OutboxRepository outboxRepository,
                           ObjectMapper objectMapper) {
        super(outboxRepository, objectMapper);
        this.currencyRepository = currencyRepository;
        this.currencyMapper = currencyMapper;
        this.cacheService = cacheService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected JpaRepository<Currency, UUID> repository() { return currencyRepository; }

    @Override
    protected CurrencyDto toDto(Currency entity) { return currencyMapper.toDto(entity); }

    @Override
    protected Currency toEntity(CurrencyDto dto) {
        Currency e = currencyMapper.toEntity(dto);
        if (e.getCurrencyId() == null) e.setCurrencyId(UUID.randomUUID());
        if (e.getCreatedDate() == null) e.setCreatedDate(Instant.now());
        return e;
    }

    @Override
    protected UUID getEntityId(Currency entity) { return entity.getCurrencyId(); }

    @Override
    protected String aggregateType() { return "CURRENCY"; }

    @Override
    protected void applyUpdateToEntity(Currency entity, CurrencyDto dto) {
        entity.setCurrencyName(dto.getCurrencyName());
        entity.setCurrencyCode(dto.getCurrencyCode());
        entity.setLastModifiedBy(dto.getLastModifiedBy());
        entity.setLastModifiedDate(Instant.now());
        entity.setActive(dto.getIsActive() == null ? entity.isActive() : dto.getIsActive());
    }

    @Override
    protected void evictCache(UUID id) {
        if (id != null) cacheService.invalidate("currency:" + id);
        cacheService.invalidate("currency:all");
    }

    @Override
    protected void validateBeforeCreate(CurrencyDto dto) {
        currencyRepository.findByCurrencyCodeIgnoreCase(dto.getCurrencyCode()).ifPresent(c -> {
            throw new ConflictException("Currency code already exists: " + dto.getCurrencyCode());
        });
    }

    @Override
    protected void validateBeforeUpdate(UUID id, CurrencyDto dto) {
        currencyRepository.findByCurrencyCodeIgnoreCase(dto.getCurrencyCode()).ifPresent(existing -> {
            if (!existing.getCurrencyId().equals(id)) {
                throw new ConflictException("Currency code already used by another currency.");
            }
        });
    }

    @Override
    protected void setInactive(Currency entity) {
        entity.setActive(false);
        entity.setLastModifiedDate(Instant.now());
    }

    // Read-through cached getters:
    public CurrencyDto findByIdCached(UUID id) {
        String key = "currency:" + id;
        return cacheService.get(key).map(json -> {
            try { return objectMapper.readValue(json, CurrencyDto.class); }
            catch (Exception e) { return null; }
        }).orElseGet(() -> {
            Currency c = currencyRepository.findById(id).orElseThrow(() -> new NotFoundException("Currency not found: " + id));
            CurrencyDto dto = currencyMapper.toDto(c);
            try { cacheService.put(key, objectMapper.writeValueAsString(dto), 3600); } catch (Exception ignored) {}
            return dto;
        });
    }

    public List<CurrencyDto> listAllCached() {
        String key = "currency:all";
        return cacheService.get(key).map(json -> {
            try {
                return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, CurrencyDto.class));
            } catch (com.fasterxml.jackson.core.JsonProcessingException | IllegalArgumentException e) {
                return null;
            }
        }).map(list -> (List<CurrencyDto>) list).orElseGet(() -> {
            var list = currencyRepository.findAllByIsActiveTrue().stream()
                .map(CurrencyMapper::toDto)
                .toList();
            try {
                cacheService.put(key, objectMapper.writeValueAsString(list), 600);
            } catch (com.fasterxml.jackson.core.JsonProcessingException | IllegalArgumentException ignored) {}
            return list;
        });
    }

    // Bulk import (simple synchronous variant)
    @Transactional
    public List<CurrencyDto> bulkCreate(List<CurrencyDto> dtos, String actor) {
        dtos.forEach(this::validateBeforeCreate);
        var entities = dtos.stream().map(this::toEntity).toList();
        var saved = currencyRepository.saveAll(entities);
        enqueueEvent(aggregateType(), null, "BULK_CREATE", dtos);
        evictCache(null);
        return saved.stream().map(CurrencyMapper::toDto).toList();
    }
}
