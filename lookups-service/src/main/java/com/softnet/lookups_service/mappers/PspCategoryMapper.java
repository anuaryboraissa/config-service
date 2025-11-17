package com.softnet.lookups_service.mappers;

import org.springframework.stereotype.Component;

import com.softnet.lookups_service.model.PspCategory;

@Component
public class PspCategoryMapper {
   public PspCategory toEntity(PspCategory entity) {
       if (entity == null)
           return null;
       PspCategory dto = new PspCategory();
       dto.setPspCategoryId(entity.getPspCategoryId());
       dto.setPspCategoryName(entity.getPspCategoryName());
       dto.setActive(entity.isActive());
       dto.setCreatedBy(entity.getCreatedBy());
       dto.setCreatedDate(entity.getCreatedDate());
       dto.setLastModifiedBy(entity.getLastModifiedBy());
       dto.setLastModifiedDate(entity.getLastModifiedDate());
       return dto;
   }

   public PspCategory toDto(PspCategory dto) {
       if (dto == null)
           return null;
       PspCategory entity = new PspCategory();
       entity.setPspCategoryId(dto.getPspCategoryId());
       entity.setPspCategoryName(dto.getPspCategoryName());
       entity.setActive(dto.isActive());
       entity.setCreatedBy(dto.getCreatedBy());
       entity.setCreatedDate(dto.getCreatedDate());
       entity.setLastModifiedBy(dto.getLastModifiedBy());
       entity.setLastModifiedDate(dto.getLastModifiedDate());
       return entity;
   }
}