package com.softnet.lookups_service.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpsertLookupRequest {
  @NotBlank
  private String key;
  @NotBlank
  private String value; // JSON string
  private String tenantId;
  private Instant effectiveFrom;
  private Instant effectiveTo;
}
