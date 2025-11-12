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
@Table(name = "lookup_def")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LookupDef {

    @Id
    private UUID id;
    @Column(unique = true, nullable = false)
    private String code;
    private String name;
    @Column(nullable = false)
    private String category; // PAYMENT, USER, etc.
    private String description;
    @Column(name = "data_type", nullable = false)
    private String dataType;
    @Column(name = "default_scope")
    private String defaultScope;
    @Column(name = "schema_json", columnDefinition = "jsonb")
    private String schemaJson;
    private String createdBy;
    private Instant createdAt = Instant.now();
}
