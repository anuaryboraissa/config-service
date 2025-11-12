package com.softnet.config_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.softnet.config_service.model.LookupValue;

@Repository
public interface LookupValueRepository extends JpaRepository<LookupValue, UUID> {

    @Query("select lv from LookupValue lv where lv.lookupDef.code = :code and (:tenantId is null or lv.tenantId = :tenantId) and lv.active = true and (lv.effectiveFrom <= current_timestamp or lv.effectiveFrom is null) and (lv.effectiveTo is null or lv.effectiveTo >= current_timestamp)")
    List<LookupValue> findActiveByCodeAndTenant(@Param("code") String code, @Param("tenantId") String tenantId);

    @Query("select lv from LookupValue lv where lv.lookupDef.code = :code and lv.key = :key and (:tenantId is null or lv.tenantId = :tenantId) and lv.active = true")
    Optional<LookupValue> findActiveByCodeKeyAndTenant(@Param("code") String code, @Param("key") String key, @Param("tenantId") String tenantId);

    @Query("select lv from LookupValue lv where lv.lookupDef.id = :lookupId order by lv.version desc")
    List<LookupValue> findByLookupIdOrderByVersionDesc(@Param("lookupId") UUID lookupId);
}
