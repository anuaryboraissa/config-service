package com.softnet.config_service.services;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softnet.config_service.dto.BulkImportDto;
import com.softnet.config_service.model.LookupDef;
import com.softnet.config_service.model.LookupValue;
import com.softnet.config_service.repository.LookupDefRepository;
import com.softnet.config_service.repository.LookupValueRepository;
import com.softnet.config_service.util.JsonSchemaValidator;

import jakarta.transaction.Transactional;

@Service
public class LookupService {

    private final LookupDefRepository defRepo;
    private final LookupValueRepository valueRepo;
    private final JsonSchemaValidator jsonSchemaValidator;
//   private final LookupEventPublisher eventPublisher;
    private final ObjectMapper mapper = new ObjectMapper();

    public LookupService(LookupDefRepository defRepo,
            LookupValueRepository valueRepo,
            JsonSchemaValidator jsonSchemaValidator
    //    ,LookupEventPublisher eventPublisher
    ) {
        this.defRepo = defRepo;
        this.valueRepo = valueRepo;
        this.jsonSchemaValidator = jsonSchemaValidator;
        // this.eventPublisher = eventPublisher;
    }

    public List<LookupValue> getActiveValues(String code, String tenantId) {
        return valueRepo.findActiveByCodeAndTenant(code, tenantId);
    }

    public Optional<LookupValue> getActiveValue(String code, String key, String tenantId) {
        return valueRepo.findActiveByCodeKeyAndTenant(code, key, tenantId);
    }

    @Transactional
    public LookupValue upsertValue(String lookupCode, String key, String tenantId, String jsonValue, String actor, Instant effectiveFrom, Instant effectiveTo) throws JsonProcessingException {
        LookupDef def = defRepo.findByCode(lookupCode).orElseThrow(() -> new NoSuchElementException("LookupDef not found: " + lookupCode));
        // validate with schema if present
        jsonSchemaValidator.validate(def.getSchemaJson(), jsonValue);

        // create new LookupValue version (we rely on manual version increment)
        LookupValue v = new LookupValue();
        v.setId(UUID.randomUUID());
        v.setLookupDef(def);
        v.setTenantId(tenantId);
        v.setKey(key);
        v.setValue(jsonValue);
        v.setMetadata(null);
        v.setEffectiveFrom(effectiveFrom == null ? Instant.now() : effectiveFrom);
        v.setEffectiveTo(effectiveTo);
        v.setActive(true);
        v.setCreatedBy(actor);
        v.setCreatedAt(Instant.now());
        // version will be set by JPA @Version on persist
        LookupValue saved = valueRepo.save(v);

        // write audit + outbox
        Map<String, Object> evt = Map.of(
                "eventType", "LOOKUP_VALUE_UPSERTED",
                "lookupCode", lookupCode,
                "tenantId", tenantId,
                "valueId", saved.getId().toString(),
                "key", key,
                "value", mapper.readTree(jsonValue)
        );
        // enqueue outbox atomically as part of the transaction (see OutboxService)
        // eventPublisher.enqueueOutbox(saved.getId(), "LOOKUP_VALUE_UPSERTED", mapper.writeValueAsString(evt));

        return saved;
    }

    @Transactional
    public void bulkImport(String lookupCode, BulkImportDto items, String actor) throws JsonProcessingException {
        for (LookupValue item : items.getLookupValues()) {
            upsertValue(lookupCode, item.getKey(), item.getTenantId(), item.getValue(), actor, item.getEffectiveFrom(), item.getEffectiveTo());
        }
    }
}
