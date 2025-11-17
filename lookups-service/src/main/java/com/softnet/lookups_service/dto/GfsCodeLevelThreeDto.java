package com.softnet.lookups_service.dto;


import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GfsCodeLevelThreeDto {
    private UUID id;
    @NotBlank @Size(max=50) private String gfsCode;
    private String gfsCodeDescription;
    @NotNull private UUID gfsCodeLevelTwoId; // parent id
    private Boolean isActive;
    private String createdBy;
    private Instant createdDate;
}

