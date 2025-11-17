package com.softnet.lookups_service.controllers;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softnet.lookups_service.dto.DistrictDto;
import com.softnet.lookups_service.services.DistrictService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/districts")
@RequiredArgsConstructor
public class DistrictController {

    private final DistrictService districtService;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_lookup.write') and hasRole('GEPG_ADMIN')")
    public ResponseEntity<DistrictDto> create(@Valid @RequestBody DistrictDto dto, Principal principal) {
        DistrictDto created = districtService.create(dto, principal == null ? "system" : principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_lookup.read')")
    public ResponseEntity<List<DistrictDto>> listAll() {
        // optionally provide pagination
        return ResponseEntity.ok(districtService.listAllCached());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_lookup.read')")
    public ResponseEntity<DistrictDto> get(@PathVariable UUID id) {
        return ResponseEntity.ok(districtService.findByIdCached(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_lookup.write') and hasRole('GEPG_ADMIN')")
    public ResponseEntity<DistrictDto> update(@PathVariable UUID id, @Valid @RequestBody DistrictDto dto, Principal principal) {
        return ResponseEntity.ok(districtService.update(id, dto, principal == null ? "system" : principal.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_lookup.admin') and hasRole('GEPG_ADMIN')")
    public ResponseEntity<Void> softDelete(@PathVariable UUID id, Principal principal) {
        districtService.softDelete(id, principal == null ? "system" : principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAuthority('SCOPE_lookup.write') and hasRole('GEPG_ADMIN')")
    public ResponseEntity<List<DistrictDto>> bulkCreate(@Valid @RequestBody List<DistrictDto> dtos, Principal principal) {
        var result = districtService.bulkCreate(dtos, principal == null ? "system" : principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
