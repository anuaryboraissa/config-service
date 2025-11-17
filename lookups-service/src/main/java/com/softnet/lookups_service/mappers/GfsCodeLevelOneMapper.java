package com.softnet.lookups_service.mappers;

import org.springframework.stereotype.Component;

import com.softnet.lookups_service.dto.GfsCodeLevelOneDto;
import com.softnet.lookups_service.model.GfsCodeLevelOne;

@Component
public final class GfsCodeLevelOneMapper {
     public GfsCodeLevelOneDto toDto(GfsCodeLevelOne entity) {
        if (entity == null) return null;
        GfsCodeLevelOneDto dto = new GfsCodeLevelOneDto();
        dto.setId(entity.getGfsCodeLevelOneId());
        dto.setGfsCode(entity.getGfsCode());
        dto.setGfsCodeDescription(entity.getGfsCodeDescription());
        dto.setIsActive(entity.isActive());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }
         public GfsCodeLevelOne toEntity(GfsCodeLevelOneDto dto) {
        if (dto == null) return null;
        GfsCodeLevelOne entity = new GfsCodeLevelOne();
        entity.setGfsCodeLevelOneId(dto.getId());
        entity.setGfsCode(dto.getGfsCode());
        entity.setGfsCodeDescription(dto.getGfsCodeDescription());
        entity.setActive(dto.getIsActive());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setCreatedDate(dto.getCreatedDate());
        return entity;
    }
}
