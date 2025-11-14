package com.softnet.config_service.mappers;

import org.springframework.stereotype.Component;

import com.softnet.config_service.dto.GfsCodeLevelTwoDto;
import com.softnet.config_service.model.GfsCodeLevelTwo;

@Component
public final class GfsCodeLevelTwoMapper {
    public GfsCodeLevelTwoDto toDto(GfsCodeLevelTwo entity){
        if(entity == null ) return null;
        
        return dto;
    }
   
}
