package com.softnet.lookups_service.mappers;

import org.springframework.stereotype.Component;

import com.softnet.lookups_service.dto.DistrictDto;
import com.softnet.lookups_service.model.District;
import com.softnet.lookups_service.model.Region;

@Component
public final class DistrictMapper {

    public DistrictDto toDto(District entity) {
        if (entity == null) return null;

        DistrictDto dto = new DistrictDto();
        dto.setDistrictId(entity.getDistrictId());
        dto.setDistrictName(entity.getDistrictName());
        dto.setRegionId(entity.getRegion() != null ? entity.getRegion().getRegionId() : null);
        dto.setIsActive(entity.isActive());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setLastModifiedBy(entity.getLastModifiedBy());
        dto.setLastModifiedDate(entity.getLastModifiedDate());
        return dto;
    }

    public District toEntity(DistrictDto dto, Region region) {
        if (dto == null) return null;

        District entity = new District();
        entity.setDistrictId(dto.getDistrictId());
        entity.setDistrictName(dto.getDistrictName());
        entity.setRegion(region);
        entity.setActive(dto.getIsActive());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setCreatedDate(dto.getCreatedDate());
        entity.setLastModifiedBy(dto.getLastModifiedBy());
        entity.setLastModifiedDate(dto.getLastModifiedDate());
        return entity;
    }
}
