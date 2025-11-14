package com.softnet.config_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "gepg", name = "gfs_codes_level_three",
       indexes = {@Index(name = "idx_gfs3_code", columnList = "gfs_code")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GfsCodeLevelThree {

    @Id
    @Column(name = "gfs_code_level_three_id", nullable = false, updatable = false)
    private UUID gfsCodeLevelThreeId;

    @Column(name = "gfs_code", nullable = false, unique = true, length = 50)
    private String gfsCode;

    @Column(name = "gfs_code_description", columnDefinition = "text")
    private String gfsCodeDescription;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gfs_code_level_two_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_gfs3_gfs2"))
    private GfsCodeLevelTwo gfsCodeLevelTwo;

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
        if (gfsCodeLevelThreeId == null) gfsCodeLevelThreeId = UUID.randomUUID();
        if (createdDate == null) createdDate = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        lastModifiedDate = Instant.now();
    }
}
