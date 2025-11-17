package com.softnet.lookups_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "gepg", name = "regions",
       indexes = {@Index(name = "idx_region_name", columnList = "region_name")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Region {

    @Id
    @Column(name = "region_id", nullable = false, updatable = false)
    private UUID regionId;

    @Column(name = "region_name", nullable = false, unique = true, length = 255)
    private String regionName;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    @PrePersist
    public void prePersist() {
        if (regionId == null) regionId = UUID.randomUUID();
        if (createdDate == null) createdDate = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        lastModifiedDate = Instant.now();
    }
}
