package com.softnet.lookups_service.util;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softnet.lookups_service.exceptions.BadRequestException;
import com.softnet.lookups_service.exceptions.NotFoundException;
import com.softnet.lookups_service.model.OutboxMessage;
import com.softnet.lookups_service.repository.OutboxRepository;

public abstract class BaseService<E, D, ID> {

    Logger logger = Logger.getLogger(BaseService.class.getName());

    protected final OutboxRepository outboxRepository;
    protected final ObjectMapper objectMapper;

    protected BaseService(OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    protected abstract JpaRepository<E, ID> repository();

    protected abstract D toDto(E entity);

    protected abstract E toEntity(D dto);

    protected abstract ID getEntityId(E entity);

    protected abstract String aggregateType(); // e.g. "CURRENCY"

    protected abstract void applyUpdateToEntity(E entity, D dto);

    // hook for cache eviction; implement in child classes
    protected abstract void evictCache(ID id);

    // hook for validation (unique constraints etc)
    protected void validateBeforeCreate(D dto) {
        /* default no-op */ }

    protected void validateBeforeUpdate(ID id, D dto) {
        /* default no-op */ }

    @Transactional
    public D create(D dto, String actor) {
        validateBeforeCreate(dto);
        E entity = toEntity(dto);
        // persist
        E saved = repository().save(entity);

        D dtoSaved = toDto(saved);

        // enqueue outbox event in same transaction
        enqueueEvent(aggregateType(), getEntityId(saved), "CREATE", dtoSaved);

        evictCache(getEntityId(saved));
        return dtoSaved;
    }

    @Transactional
    public D update(ID id, D dto, String actor) {
        repository().findById(id).orElseThrow(() -> new NotFoundException(aggregateType() + " not found: " + id));
        validateBeforeUpdate(id, dto);

        E entity = repository().getById(id);
        applyUpdateToEntity(entity, dto);
        E updated = repository().save(entity);
        D dtoUpdated = toDto(updated);

        enqueueEvent(aggregateType(), getEntityId(updated), "UPDATE", dtoUpdated);
        evictCache(getEntityId(updated));
        return dtoUpdated;
    }

    @Transactional
    public void softDelete(ID id, String actor) {
        E entity = repository().findById(id).orElseThrow(() -> new NotFoundException(aggregateType() + " not found: " + id));
        try {
            // Expect entity has setActive or similar; reflect in implementation
            // Use reflection or child-tailored method; prefer child override
            setInactive(entity);
            repository().save(entity);
            enqueueEvent(aggregateType(), id, "DEACTIVATE", null);
            evictCache(id);
        } catch (UnsupportedOperationException ex) {
            throw new BadRequestException("Soft delete not supported for " + aggregateType());
        }
    }

    // by default throw: child can override for supporting soft delete
    protected void setInactive(E entity) {
        throw new UnsupportedOperationException("Soft delete not supported");
    }

    @Transactional
    public void hardDelete(ID id, String actor) {
        E entity = repository().findById(id).orElseThrow(() -> new NotFoundException(aggregateType() + " not found: " + id));
        repository().delete(entity);
        enqueueEvent(aggregateType(), id, "DELETE", null);
        evictCache(id);
    }

    @Transactional
    public List<D> bulkCreate(List<D> dtos, String actor) {
        List<E> entities = dtos.stream().map(this::toEntity).collect(Collectors.toList());
        List<E> saved = repository().saveAll(entities);
        List<D> dtosSaved = saved.stream().map(this::toDto).collect(Collectors.toList());
        // create one bulk event with payload = list of dtos
        enqueueEvent(aggregateType(), null, "BULK_CREATE", dtosSaved);
        // evict coarse cache
        evictCache(null);
        return dtosSaved;
    }

    protected void enqueueEvent(String aggregateType, ID aggregateId, String eventType, Object payload) {
        try {
            logger.log(Level.INFO, "Enqueue outbox event payload: {0}", payload);
            OutboxMessage msg = OutboxMessage.builder()
                    .id(UUID.randomUUID())
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId instanceof UUID ? (UUID) aggregateId : null)
                    .eventType(eventType)
                    .payload(payload)
                    .status("PENDING") // explicitly set default
                    .attempts(0) // initialize attempts
                    .published(false)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            logger.log(Level.INFO, "Created outbox message: {0}", msg);
            outboxRepository.save(msg);
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Failed to convert outbox payload: {0}", e.getMessage());
            throw new RuntimeException("Failed to enqueue outbox event", e);
        }
    }
}
