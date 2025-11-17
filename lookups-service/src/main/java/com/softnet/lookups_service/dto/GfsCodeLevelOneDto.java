package com.softnet.lookups_service.dto;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GfsCodeLevelOneDto {
    private UUID id;
    @NotBlank @Size(max=50) private String gfsCode;
    private String gfsCodeDescription;
    private Boolean isActive;
    private String createdBy;
    private Instant createdDate;
}
