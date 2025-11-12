package com.softnet.config_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "lookup_value",
        uniqueConstraints = @UniqueConstraint(columnNames = {"lookup_id", "key", "tenant_id", "version"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LookupValue {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lookup_id", nullable = false)
    private LookupDef lookupDef;

    private String tenantId;

    @Column(name = "key", nullable = false)
    private String key;

    @Column(columnDefinition = "jsonb", name = "value", nullable = false)
    private String value; // store JSON as String (Jackson)

    @Column(columnDefinition = "jsonb")
    private String metadata;

    private Instant effectiveFrom;
    private Instant effectiveTo;

    @Version
    private Integer version; // optimistic locking -> hibernate's @Version maps to integer column

    private boolean active = true;
    private String createdBy;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
}
