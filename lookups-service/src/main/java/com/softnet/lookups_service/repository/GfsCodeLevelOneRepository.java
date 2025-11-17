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

import com.softnet.lookups_service.model.GfsCodeLevelOne;

@Repository
public interface GfsCodeLevelOneRepository extends JpaRepository<GfsCodeLevelOne, UUID> {
    // Lookup by exact code

    Optional<GfsCodeLevelOne> findByGfsCodeIgnoreCase(String code);

    // Search by partial code or description
    List<GfsCodeLevelOne> findByGfsCodeContainingIgnoreCaseOrGfsCodeDescriptionContainingIgnoreCase(String code, String description);

    // Fetch all active
    List<GfsCodeLevelOne> findByIsActiveTrue();

    // Fetch all inactive
    List<GfsCodeLevelOne> findByIsActiveFalse();

    // Bulk activate/deactivate
    @Modifying
    @Query("UPDATE GfsCodeLevelOne g SET g.isActive = :active WHERE g.gfsCodeLevelOneId IN :ids")
    int updateActiveStatus(@Param("active") boolean active, @Param("ids") List<UUID> ids);

    // Soft delete (just deactivate)
    default int softDelete(List<UUID> ids) {
        return updateActiveStatus(false, ids);
    }

    // Check existence by code
    boolean existsByGfsCodeIgnoreCase(String code);

    // Pagination + search
    Page<GfsCodeLevelOne> findByGfsCodeContainingIgnoreCaseOrGfsCodeDescriptionContainingIgnoreCase(String code, String description, Pageable pageable);
}
