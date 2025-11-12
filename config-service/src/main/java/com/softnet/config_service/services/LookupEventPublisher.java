package com.softnet.config_service.services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import com.softnet.config_service.model.OutboxMessage;
import com.softnet.config_service.repository.OutboxRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class LookupEventPublisher {
  private final OutboxRepository outboxRepo;
  public LookupEventPublisher(OutboxRepository outboxRepo) { this.outboxRepo = outboxRepo; }

  @Transactional(propagation = Propagation.MANDATORY)
  public void enqueueOutbox(UUID aggregateId, String eventType, String payload) {
    OutboxMessage msg = new OutboxMessage();
    msg.setId(UUID.randomUUID());
    msg.setAggregateId(aggregateId);
    msg.setAggregateType("lookup_value");
    msg.setEventType(eventType);
    msg.setPayload(payload);
    msg.setCreatedAt(Instant.now());
    outboxRepo.save(msg);
  }
  // background publisher method (not transactional) will be below
}
