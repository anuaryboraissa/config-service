package com.softnet.config_service.mappers;

import com.softnet.config_service.dto.CurrencyDto;
import com.softnet.config_service.model.Currency;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public final class CurrencyMapper {
    public static CurrencyDto toDto(Currency e) {
        if (e == null) return null;
        return CurrencyDto.builder()
                .currencyId(e.getCurrencyId())
                .currencyName(e.getCurrencyName())
                .currencyCode(e.getCurrencyCode())
                .isActive(e.isActive())
                .createdBy(e.getCreatedBy())
                .createdDate(e.getCreatedDate())
                .lastModifiedBy(e.getLastModifiedBy())
                .lastModifiedDate(e.getLastModifiedDate())
                .build();
    }

    public static Currency toEntity(CurrencyDto dto) {
        if (dto == null) return null;
        return Currency.builder()
                .currencyId(dto.getCurrencyId() != null ? dto.getCurrencyId() : UUID.randomUUID())
                .currencyName(dto.getCurrencyName())
                .currencyCode(dto.getCurrencyCode())
                .isActive(dto.getIsActive() == null ? true : dto.getIsActive())
                .createdBy(dto.getCreatedBy())
                .createdDate(dto.getCreatedDate())
                .lastModifiedBy(dto.getLastModifiedBy())
                .lastModifiedDate(dto.getLastModifiedDate())
                .build();
    }
}
