package com.softnet.lookups_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.softnet.lookups_service.model.OutboxMessage;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxMessage, UUID> {

    /**
     * Get unpublished messages (published=false), ordered by createdAt
     * Useful for outbox publisher batch processing
     */
    List<OutboxMessage> findByPublishedFalseOrderByCreatedAtAsc(Pageable pageable);

    /**
     * Get pending messages (status=PENDING)
     */
    @Query("SELECT m FROM OutboxMessage m WHERE m.status = 'PENDING' ORDER BY m.createdAt ASC")
    List<OutboxMessage> findPendingMessages(Pageable pageable);

    /**
     * Get failed messages that are retryable (attempts < maxRetries)
     * COALESCE ensures ordering even if lastAttemptAt is null
     */
    @Query("""
       SELECT m 
       FROM OutboxMessage m
       WHERE m.status = 'FAILED' AND m.attempts < :maxRetries
       ORDER BY COALESCE(m.lastAttemptAt, m.createdAt) ASC
       """)
    List<OutboxMessage> findRetryableMessages(@Param("maxRetries") int maxRetries, Pageable pageable);

    /**
     * Combine pending and retryable messages for batch processing
     */
    @Query("""
       SELECT m
       FROM OutboxMessage m
       WHERE m.status = 'PENDING'
          OR (m.status = 'FAILED' AND m.attempts < :maxRetries)
       ORDER BY COALESCE(m.lastAttemptAt, m.createdAt) ASC
       """)
    List<OutboxMessage> findPendingAndRetryable(@Param("maxRetries") int maxRetries, Pageable pageable);

    /**
     * DLQ messages ordered by dlqAt timestamp
     */
    List<OutboxMessage> findByStatusOrderByDlqAtAsc(String status);

}
