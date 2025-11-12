package com.softnet.config_service.dto;

import com.softnet.config_service.model.LookupValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LookupValueDto {

    private UUID id;
    private String tenantId;
    private String lookupId; // reference to LookupDef
    private String key;
    private String value; // JSON stored as string
    private String metadata;
    private Instant effectiveFrom;
    private Instant effectiveTo;
    private Integer version;
    private boolean active;
    private String createdBy;
    private Instant createdAt;
    private Instant updatedAt;

    // ✅ Static mapper from entity → DTO
    public static LookupValueDto from(LookupValue entity) {
        if (entity == null) return null;

        LookupValueDto dto = new LookupValueDto();
        dto.setId(entity.getId());
        dto.setTenantId(entity.getTenantId());
        // Safely extract lookupDef ID if available
        dto.setLookupId(entity.getLookupDef() != null ? entity.getLookupDef().getId().toString() : null);
        dto.setKey(entity.getKey());
        dto.setValue(entity.getValue());
        dto.setMetadata(entity.getMetadata());
        dto.setEffectiveFrom(entity.getEffectiveFrom());
        dto.setEffectiveTo(entity.getEffectiveTo());
        dto.setVersion(entity.getVersion());
        dto.setActive(entity.isActive());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    // ✅ Optional: mapper from DTO → entity (used in POST/PUT)
    public LookupValue toEntity() {
        LookupValue entity = new LookupValue();
        entity.setId(this.id != null ? this.id : UUID.randomUUID());
        entity.setTenantId(this.tenantId);
        entity.setKey(this.key);
        entity.setValue(this.value);
        entity.setMetadata(this.metadata);
        entity.setEffectiveFrom(this.effectiveFrom);
        entity.setEffectiveTo(this.effectiveTo);
        entity.setVersion(this.version);
        entity.setActive(this.active);
        entity.setCreatedBy(this.createdBy);
        entity.setCreatedAt(this.createdAt != null ? this.createdAt : Instant.now());
        entity.setUpdatedAt(Instant.now());
        return entity;
    }
}
