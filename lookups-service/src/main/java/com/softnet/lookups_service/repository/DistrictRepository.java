package com.softnet.lookups_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softnet.lookups_service.model.District;

@Repository
public interface DistrictRepository extends JpaRepository<District, UUID> {

    List<District> findByRegionRegionId(UUID regionId);

    List<District> findAllByIsActiveTrue();

    Optional<District> findByDistrictNameIgnoreCase(String name);

    boolean existsByDistrictNameIgnoreCase(String name);

    boolean existsByRegionRegionId(UUID regionId);
}
