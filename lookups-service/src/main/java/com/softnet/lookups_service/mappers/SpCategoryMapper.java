package com.softnet.lookups_service.mappers;

import org.springframework.stereotype.Component;

import com.softnet.lookups_service.dto.SpCategoryDto;
import com.softnet.lookups_service.model.SpCategory;

@Component
public class SpCategoryMapper {
  public SpCategory toEntity(SpCategoryDto dto) {
      if (dto == null)
          return null;
      SpCategory entity = new SpCategory();
      entity.setSpCategoryId(dto.getSpCategoryId());
      entity.setSpCategoryName(dto.getSpCategoryName());
      entity.setActive(dto.getIsActive());
      entity.setCreatedBy(dto.getCreatedBy());
      entity.setCreatedDate(dto.getCreatedDate());
      entity.setLastModifiedBy(dto.getLastModifiedBy());
      entity.setLastModifiedDate(dto.getLastModifiedDate());
      return entity;
  }

  public SpCategoryDto toDto(SpCategory entity) {
      if (entity == null)
          return null;
      SpCategoryDto dto = new SpCategoryDto();
      dto.setSpCategoryId(entity.getSpCategoryId());
      dto.setSpCategoryName(entity.getSpCategoryName());
      dto.setIsActive(entity.isActive());
      dto.setCreatedBy(entity.getCreatedBy());
      dto.setCreatedDate(entity.getCreatedDate());
      dto.setLastModifiedBy(entity.getLastModifiedBy());
      dto.setLastModifiedDate(entity.getLastModifiedDate());
      return dto;
  }
}