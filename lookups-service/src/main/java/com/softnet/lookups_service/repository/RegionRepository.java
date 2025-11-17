package com.softnet.lookups_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softnet.lookups_service.model.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, UUID> {

    Optional<Region> findByRegionNameIgnoreCase(String name);

    List<Region> findAllByIsActiveTrue();

    boolean existsByRegionNameIgnoreCase(String name);
}
