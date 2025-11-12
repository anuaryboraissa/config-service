package com.softnet.config_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softnet.config_service.model.LookupDef;

@Repository
public interface LookupDefRepository extends JpaRepository<LookupDef, UUID> {

    Optional<LookupDef> findByCode(String code);
}
