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

import com.softnet.lookups_service.model.GfsCodeLevelThree;

@Repository
public interface GfsCodeLevelThreeRepository extends JpaRepository<GfsCodeLevelThree, UUID> {
    // Find children by parent

    List<GfsCodeLevelThree> findByGfsCodeLevelTwo_GfsCodeLevelTwoId(UUID parentId);

    // Find exact code
    Optional<GfsCodeLevelThree> findByGfsCodeIgnoreCase(String code);

    // Search by code/description
    List<GfsCodeLevelThree> findByGfsCodeContainingIgnoreCaseOrGfsCodeDescriptionContainingIgnoreCase(String code, String description);

    // Fetch active/inactive
    List<GfsCodeLevelThree> findByIsActiveTrue();

    List<GfsCodeLevelThree> findByIsActiveFalse();

    // Bulk activate/deactivate
    @Modifying
    @Query("UPDATE GfsCodeLevelThree g SET g.isActive = :active WHERE g.gfsCodeLevelThreeId IN :ids")
    int updateActiveStatus(@Param("active") boolean active, @Param("ids") List<UUID> ids);

    // Soft delete
    default int softDelete(List<UUID> ids) {
        return updateActiveStatus(false, ids);
    }

    // Check existence
    boolean existsByGfsCodeIgnoreCase(String code);

    // Pagination + search
    Page<GfsCodeLevelThree> findByGfsCodeContainingIgnoreCaseOrGfsCodeDescriptionContainingIgnoreCase(String code, String description, Pageable pageable);

    // Check if parent exists before insert
    boolean existsByGfsCodeLevelTwo_GfsCodeLevelTwoId(UUID levelTwoId);
}
