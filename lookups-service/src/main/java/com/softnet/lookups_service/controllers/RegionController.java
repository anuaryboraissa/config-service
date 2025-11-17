package com.softnet.lookups_service.controllers;


import java.security.Principal;
import java.util.List;
import java.util.Map;
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
import com.softnet.lookups_service.dto.RegionDto;
import com.softnet.lookups_service.services.DistrictService;
import com.softnet.lookups_service.services.RegionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;
    private final DistrictService districtService;
    // private final DistrictMapper districtMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_lookup.write') and hasRole('GEPG_ADMIN')")
    public ResponseEntity<RegionDto> create(@Valid @RequestBody RegionDto dto, Principal principal) {
        RegionDto created = regionService.create(dto, principal == null ? "system" : principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_lookup.read')")
    public ResponseEntity<List<RegionDto>> listAll() {
        return ResponseEntity.ok(regionService.listAllCached());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_lookup.read')")
    public ResponseEntity<RegionDto> get(@PathVariable UUID id) {
        return ResponseEntity.ok(regionService.findByIdCached(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_lookup.write') and hasRole('GEPG_ADMIN')")
    public ResponseEntity<RegionDto> update(@PathVariable UUID id, @Valid @RequestBody RegionDto dto, Principal principal) {
        return ResponseEntity.ok(regionService.update(id, dto, principal == null ? "system" : principal.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_lookup.admin') and hasRole('GEPG_ADMIN')")
    public ResponseEntity<Void> softDelete(@PathVariable UUID id, Principal principal) {
        regionService.softDelete(id, principal == null ? "system" : principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/districts")
    @PreAuthorize("hasAuthority('SCOPE_lookup.read')")
    public ResponseEntity<List<DistrictDto>> getDistricts(@PathVariable UUID id) {
        return ResponseEntity.ok(districtService.listByRegionCached(id));
    }

    // convenience: get region with districts
    @GetMapping("/{id}/with-districts")
    @PreAuthorize("hasAuthority('SCOPE_lookup.read')")
    public ResponseEntity<?> getRegionWithDistricts(@PathVariable UUID id) {
        var region = regionService.findByIdCached(id);
        var districts = districtService.listByRegionCached(id);
        return ResponseEntity.ok(Map.of("region", region, "districts", districts));
    }
}
