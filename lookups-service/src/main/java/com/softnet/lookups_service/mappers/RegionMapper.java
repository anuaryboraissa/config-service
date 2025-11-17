package com.softnet.lookups_service.mappers;

import com.softnet.lookups_service.dto.RegionDto;
import com.softnet.lookups_service.model.Region;

import org.springframework.stereotype.Component;

@Component
public final class RegionMapper {
    public RegionDto toDto(Region entity) {
        if (entity == null) return null;
        RegionDto dto = new RegionDto();
        dto.setRegionId(entity.getRegionId());
        dto.setRegionName(entity.getRegionName());
        dto.setIsActive(entity.isActive());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setLastModifiedBy(entity.getLastModifiedBy());
        dto.setLastModifiedDate(entity.getLastModifiedDate());
        return dto;
    }

    public Region toEntity(RegionDto dto) {
        if (dto == null) return null;
        Region entity = new Region();
        entity.setRegionId(dto.getRegionId());
        entity.setRegionName(dto.getRegionName());
        entity.setActive(dto.getIsActive());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setCreatedDate(dto.getCreatedDate());
        entity.setLastModifiedBy(dto.getLastModifiedBy());
        entity.setLastModifiedDate(dto.getLastModifiedDate());
        return entity;
    }
}
