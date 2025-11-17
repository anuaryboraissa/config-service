package com.softnet.lookups_service.model;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.Type;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "outbox_message", schema = "gepg")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxMessage {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "aggregate_type")
    private String aggregateType;

    @Column(name = "aggregate_id")
    private UUID aggregateId;

    @Column(name = "event_type")
    private String eventType;

    // @Column(name = "payload", columnDefinition = "jsonb")
    // @Convert(converter = JsonbConverter.class)
    // private Object payload;
    @Column(name = "payload", columnDefinition = "jsonb")
    @Type(JsonBinaryType.class)
    private Object payload;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, PUBLISHED, FAILED, DLQ

    @Column(nullable = false)
    private int attempts = 0;

    @Column
    private Instant lastAttemptAt;

    @Column
    private String lastErrorMessage;

    @Column
    private Instant dlqAt;

    @Column
    private String resolvedBy;

    @Column
    private Instant resolvedAt;

    @Column
    private String resolutionNotes;

    @Column(name = "published", nullable = false)
    private boolean published;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "published_at")
    private Instant publishedAt;
}
