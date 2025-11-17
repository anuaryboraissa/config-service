package com.softnet.lookups_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.softnet.lookups_service.model.Currency;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, UUID> {

    // Optional<Currency> findByCurrencyCodeIgnoreCase(String currencyCode);
    Optional<Currency> findByCurrencyCodeIgnoreCase(String currencyCode);


    List<Currency> findAllByIsActiveTrue();

    @Query("select c from Currency c where lower(c.currencyName) like lower(concat('%', :term, '%')) or lower(c.currencyCode) like lower(concat('%', :term, '%'))")
    Page<Currency> searchByTerm(@Param("term") String term, Pageable pageable);
}
