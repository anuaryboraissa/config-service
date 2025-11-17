package com.softnet.lookups_service.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "gepg", name = "gfs_codes_level_one",
       indexes = {@Index(name = "idx_gfs1_code", columnList = "gfs_code")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GfsCodeLevelOne {
    @Id
    @Column(name = "gfs_code_level_one_id", nullable = false, updatable = false)
    private UUID gfsCodeLevelOneId;

    @Column(name = "gfs_code", nullable = false, unique = true, length = 50)
    private String gfsCode;

    @Column(name = "gfs_code_description", columnDefinition = "text")
    private String gfsCodeDescription;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

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
        if (gfsCodeLevelOneId == null) gfsCodeLevelOneId = UUID.randomUUID();
        if (createdDate == null) createdDate = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        lastModifiedDate = Instant.now();
    }
}
