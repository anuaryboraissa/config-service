package com.softnet.config_service.util;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JsonSchemaValidator {

    private final ObjectMapper mapper = new ObjectMapper();

    public void validate(String schemaJson, String instanceJson) {
        if (schemaJson == null || schemaJson.isBlank()) {
            return; // no schema -> accept

                }try {
            JSONObject rawSchema = new JSONObject(schemaJson);
            Schema schema = SchemaLoader.load(rawSchema);
            JSONObject instance = new JSONObject(instanceJson);
            schema.validate(instance);
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON schema validation failed: " + e.getMessage(), e);
        }
    }
}
