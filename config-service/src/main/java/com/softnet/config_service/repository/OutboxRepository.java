package com.softnet.config_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.softnet.config_service.model.OutboxMessage;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxMessage, UUID> {
    List<OutboxMessage> findByPublishedFalseOrderByCreatedAtAsc(Pageable pageable);
}
