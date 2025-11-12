package com.softnet.config_service.controllers;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.softnet.config_service.dto.BulkImportDto;
import com.softnet.config_service.dto.LookupValueDto;
import com.softnet.config_service.dto.UpsertLookupRequest;
import com.softnet.config_service.model.LookupValue;
import com.softnet.config_service.services.LookupService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/lookups")
@RequiredArgsConstructor
public class LookupController {
   private final LookupService lookupService;

  @GetMapping("/{code}")
  @PreAuthorize("hasAuthority('SCOPE_lookup.read')")
  public ResponseEntity<List<LookupValueDto>> getValues(@PathVariable String code, @RequestParam(required=false) String tenant) {
    var values = lookupService.getActiveValues(code, tenant);
    return ResponseEntity.ok(values.stream().map(LookupValueDto::from).collect(Collectors.toList()));
  }

  @PostMapping("/{code}/values")
  @PreAuthorize("hasAuthority('SCOPE_lookup.write') and hasRole('GEPG_ADMIN')") // example
  public ResponseEntity<LookupValueDto> upsert(@PathVariable String code,
                                               @Valid @RequestBody UpsertLookupRequest request,
                                               Principal principal) throws JsonProcessingException {
    LookupValue saved = lookupService.upsertValue(code, request.getKey(), request.getTenantId(), request.getValue(), principal.getName(), request.getEffectiveFrom(), request.getEffectiveTo());
    return ResponseEntity.status(HttpStatus.CREATED).body(LookupValueDto.from(saved));
  }

  @PostMapping("/{code}/bulk")
  @PreAuthorize("hasAuthority('SCOPE_lookup.write') and hasRole('GEPG_ADMIN')")
  public ResponseEntity<Void> bulkImport(@PathVariable String code, @RequestBody BulkImportDto items, Principal principal) throws JsonProcessingException {
    lookupService.bulkImport(code, items, principal.getName());
    return ResponseEntity.accepted().build();
  }
}



