package com.softnet.config_service.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "outbox_message")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutboxMessage {
    @Id
    private UUID id;
    private String aggregateType;
    private UUID aggregateId;
    private String eventType;
    @Column(columnDefinition = "jsonb")
    private String payload;
    private boolean published = false;
    private Instant createdAt = Instant.now();
    private Instant publishedAt;
}
