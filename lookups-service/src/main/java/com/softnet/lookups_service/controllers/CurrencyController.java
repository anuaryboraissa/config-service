package com.softnet.lookups_service.controllers;


import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softnet.lookups_service.dto.CurrencyDto;
import com.softnet.lookups_service.services.CurrencyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/currencies")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @PostMapping
    // @PreAuthorize("hasAuthority('SCOPE_lookup.write') and hasRole('GEPG_ADMIN')")
    public ResponseEntity<CurrencyDto> create(@Valid @RequestBody CurrencyDto dto, Principal principal) {
        CurrencyDto created = currencyService.create(dto, principal == null ? "system" : principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    // @PreAuthorize("hasAuthority('SCOPE_lookup.read')")
    public ResponseEntity<List<CurrencyDto>> listAll() {
        return ResponseEntity.ok(currencyService.listAllCached());
    }

    @GetMapping("/{id}")
    // @PreAuthorize("hasAuthority('SCOPE_lookup.readq')")
    public ResponseEntity<CurrencyDto> get(@PathVariable UUID id) {
        return ResponseEntity.ok(currencyService.findByIdCached(id));
    }

    @PutMapping("/{id}")
    // @PreAuthorize("hasAuthority('SCOPE_lookup.write') and hasRole('GEPG_ADMIN')")
    public ResponseEntity<CurrencyDto> update(@PathVariable UUID id, @Valid @RequestBody CurrencyDto dto, Principal principal) {
        return ResponseEntity.ok(currencyService.update(id, dto, principal == null ? "system" : principal.getName()));
    }

    @DeleteMapping("/{id}")
    // @PreAuthorize("hasAuthority('SCOPE_lookup.admin') and hasRole('GEPG_ADMIN')")
    public ResponseEntity<Void> softDelete(@PathVariable UUID id, Principal principal) {
        currencyService.softDelete(id, principal == null ? "system" : principal.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/hard")
    // @PreAuthorize("hasAuthority('SCOPE_lookup.admin') and hasRole('GEPG_SUPERADMIN')")
    public ResponseEntity<Void> hardDelete(@PathVariable UUID id, Principal principal) {
        currencyService.hardDelete(id, principal == null ? "system" : principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    // @PreAuthorize("hasAuthority('SCOPE_lookup.write') and hasRole('GEPG_ADMIN')")
    public ResponseEntity<List<CurrencyDto>> bulkCreate(@Valid @RequestBody List<CurrencyDto> dtos, Principal principal) {
        var created = currencyService.bulkCreate(dtos, principal == null ? "system" : principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
