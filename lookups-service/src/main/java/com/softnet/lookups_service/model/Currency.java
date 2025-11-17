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
@Table(schema = "gepg", name = "currencies",
       indexes = {
         @Index(name = "idx_currency_code", columnList = "currency_code"),
         @Index(name = "idx_currency_name", columnList = "currency_name")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Currency {
 @Id
    @Column(name = "currency_id", nullable = false, updatable = false)
    private UUID currencyId;

    @Column(name = "currency_name", nullable = false, unique = true, length = 50)
    private String currencyName;

    @Column(name = "currency_code", nullable = false, unique = true, length = 10)
    private String currencyCode;

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
        if (currencyId == null) currencyId = UUID.randomUUID();
        if (createdDate == null) createdDate = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        lastModifiedDate = Instant.now();
    }
}
