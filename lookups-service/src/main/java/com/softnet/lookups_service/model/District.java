package com.softnet.lookups_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "gepg", name = "districts",
       indexes = {@Index(name = "idx_district_name", columnList = "district_name")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class District {

    @Id
    @Column(name = "district_id", nullable = false, updatable = false)
    private UUID districtId;

    @Column(name = "district_name", nullable = false, unique = true, length = 255)
    private String districtName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "region_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_district_region"))
    private Region region;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate;

    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    @PrePersist
    public void prePersist() {
        if (districtId == null) districtId = UUID.randomUUID();
        if (createdDate == null) createdDate = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        lastModifiedDate = Instant.now();
    }
}
