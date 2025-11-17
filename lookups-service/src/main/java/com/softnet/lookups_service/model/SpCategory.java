package com.softnet.lookups_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "gepg", name = "sp_category",
       indexes = {@Index(name = "idx_sp_category_name", columnList = "sp_category_name")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpCategory {

    @Id
    @Column(name = "sp_category_id", nullable = false, updatable = false)
    private UUID spCategoryId;

    @Column(name = "sp_category_name", nullable = false, unique = true, length = 50)
    private String spCategoryName;

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
        if (spCategoryId == null) spCategoryId = UUID.randomUUID();
        if (createdDate == null) createdDate = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        lastModifiedDate = Instant.now();
    }
}
