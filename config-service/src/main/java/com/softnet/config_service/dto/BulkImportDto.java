package com.softnet.config_service.dto;

import java.util.List;

import com.softnet.config_service.model.LookupValue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkImportDto {
    private List<LookupValue> lookupValues;

}
