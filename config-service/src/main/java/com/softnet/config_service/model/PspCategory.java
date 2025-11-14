package com.softnet.config_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "gepg", name = "psp_category",
       indexes = {@Index(name = "idx_psp_category_name", columnList = "psp_category_name")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PspCategory {

    @Id
    @Column(name = "psp_category_id", nullable = false, updatable = false)
    private UUID pspCategoryId;

    @Column(name = "psp_category_name", nullable = false, unique = true, length = 50)
    private String pspCategoryName;

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
        if (pspCategoryId == null) pspCategoryId = UUID.randomUUID();
        if (createdDate == null) createdDate = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        lastModifiedDate = Instant.now();
    }
}
