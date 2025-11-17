package com.softnet.lookups_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.softnet.lookups_service.model.GfsCodeLevelTwo;

@Repository
public interface GfsCodeLevelTwoRepository extends JpaRepository<GfsCodeLevelTwo, UUID>{

    // Find children by parent
    List<GfsCodeLevelTwo> findByGfsCodeLevelOne_GfsCodeLevelOneId(UUID parentId);

    // Find exact code
    Optional<GfsCodeLevelTwo> findByGfsCodeIgnoreCase(String code);

    // Search by code/description
    List<GfsCodeLevelTwo> findByGfsCodeContainingIgnoreCaseOrGfsCodeDescriptionContainingIgnoreCase(String code, String description);

    // Fetch active/inactive
    List<GfsCodeLevelTwo> findByIsActiveTrue();
    List<GfsCodeLevelTwo> findByIsActiveFalse();

    // Bulk activate/deactivate
    @Modifying
    @Query("UPDATE GfsCodeLevelTwo g SET g.isActive = :active WHERE g.gfsCodeLevelTwoId IN :ids")
    int updateActiveStatus(@Param("active") boolean active, @Param("ids") List<UUID> ids);

    // Soft delete
    default int softDelete(List<UUID> ids) {
        return updateActiveStatus(false, ids);
    }

    // Check existence
    boolean existsByGfsCodeIgnoreCase(String code);

    // Pagination + search
    Page<GfsCodeLevelTwo> findByGfsCodeContainingIgnoreCaseOrGfsCodeDescriptionContainingIgnoreCase(String code, String description, Pageable pageable);

    // Check if parent exists before insert
    boolean existsByGfsCodeLevelOne_GfsCodeLevelOneId(UUID levelOneId);
}
