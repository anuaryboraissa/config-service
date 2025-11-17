package com.softnet.lookups_service.mappers;

import org.springframework.stereotype.Component;

import com.softnet.lookups_service.model.GfsCodeLevelThree;

@Component
public class GfsCodeLevelThreeMapper {

    public GfsCodeLevelThree toEntity(GfsCodeLevelThree entity) {
        if (entity == null)
            return null;
        GfsCodeLevelThree dto = new GfsCodeLevelThree();
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setGfsCode(entity.getGfsCode());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setGfsCodeDescription(entity.getGfsCodeDescription());
        dto.setGfsCodeLevelThreeId(entity.getGfsCodeLevelThreeId());
        dto.setActive(entity.isActive());
        dto.setLastModifiedBy(entity.getLastModifiedBy());
        dto.setLastModifiedDate(entity.getLastModifiedDate());
        return dto;
    }

    public GfsCodeLevelThree toDto(GfsCodeLevelThree dto) {
        if (dto == null)
            return null;
        GfsCodeLevelThree entity = new GfsCodeLevelThree();
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setGfsCode(dto.getGfsCode());
        entity.setCreatedDate(dto.getCreatedDate());
        entity.setGfsCodeDescription(dto.getGfsCodeDescription());
        entity.setGfsCodeLevelThreeId(dto.getGfsCodeLevelThreeId());
        entity.setActive(dto.isActive());
        entity.setLastModifiedBy(dto.getLastModifiedBy());
        entity.setLastModifiedDate(dto.getLastModifiedDate());
        return entity;
    }

}
