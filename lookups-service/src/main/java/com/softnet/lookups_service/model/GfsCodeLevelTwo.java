package com.softnet.lookups_service.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "gepg", name = "gfs_codes_level_two",
       indexes = {@Index(name = "idx_gfs2_code", columnList = "gfs_code")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GfsCodeLevelTwo {

    @Id
    @Column(name = "gfs_code_level_two_id", nullable = false, updatable = false)
    private UUID gfsCodeLevelTwoId;

    @Column(name = "gfs_code", nullable = false, unique = true, length = 50)
    private String gfsCode;

    @Column(name = "gfs_code_description", columnDefinition = "text")
    private String gfsCodeDescription;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gfs_code_level_one_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_gfs2_gfs1"))
    private GfsCodeLevelOne gfsCodeLevelOne;

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
        if (gfsCodeLevelTwoId == null) gfsCodeLevelTwoId = UUID.randomUUID();
        if (createdDate == null) createdDate = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        lastModifiedDate = Instant.now();
    }
}
