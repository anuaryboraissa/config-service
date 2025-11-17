package com.softnet.lookups_service.mappers;

import org.springframework.stereotype.Component;

import com.softnet.lookups_service.dto.GfsCodeLevelTwoDto;
import com.softnet.lookups_service.model.GfsCodeLevelTwo;

@Component
public final class GfsCodeLevelTwoMapper {
    public GfsCodeLevelTwoDto toDto(GfsCodeLevelTwo entity){
        if(entity == null ) return null;
        GfsCodeLevelTwoDto dto = new GfsCodeLevelTwoDto();
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setGfsCode(entity.getGfsCode());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setGfsCodeDescription(entity.getGfsCodeDescription());
        dto.setGfsCodeLevelTwoId(entity.getGfsCodeLevelTwoId());
        dto.setIsActive(entity.isActive());
        dto.setLastModifiedBy(entity.getLastModifiedBy());
        dto.setLastModifiedDate(entity.getLastModifiedDate());
        return dto;
    }

    public GfsCodeLevelTwo toEntity(GfsCodeLevelTwoDto dto){
        if(dto == null) return null;
        GfsCodeLevelTwo entity = new GfsCodeLevelTwo();
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setGfsCode(dto.getGfsCode());
        entity.setCreatedDate(dto.getCreatedDate());
        entity.setGfsCodeDescription(dto.getGfsCodeDescription());
        entity.setGfsCodeLevelTwoId(dto.getGfsCodeLevelTwoId());
        entity.setActive(dto.getIsActive());
        entity.setLastModifiedBy(dto.getLastModifiedBy());
        entity.setLastModifiedDate(dto.getLastModifiedDate());
        return entity;
    }
   
}
