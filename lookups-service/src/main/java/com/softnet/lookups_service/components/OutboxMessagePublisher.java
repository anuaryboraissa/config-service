package com.softnet.lookups_service.components;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.softnet.lookups_service.model.OutboxMessage;
import com.softnet.lookups_service.repository.OutboxRepository;
import com.softnet.lookups_service.services.LookupProducer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OutboxMessagePublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxMessagePublisher.class);

    private final OutboxRepository repository;

    private final LookupProducer producer;

    private static final int BATCH = 100;
    private static final int MAX_RETRIES = 20;
    private static final long BASE_BACKOFF_MS = 2000;

    @Scheduled(fixedDelayString = "${outbox.publish.delay.ms:2000}")
    public void publishCycle() {
        List<OutboxMessage> messages
                = repository.findPendingAndRetryable(MAX_RETRIES, PageRequest.of(0, BATCH));
        messages.forEach(this::processMessageSafely);
    }

    private void processMessageSafely(OutboxMessage msg) {
        try {
            processMessage(msg);
        } catch (Exception ex) {
            log.error("Error publishing message {}", msg.getId(), ex);
        }
    }

    @Transactional
    public void processMessage(OutboxMessage msg) {

        if (!readyForRetry(msg)) {
            return;
        }

        try {
            // String payloadStr = msg.getPayload() != null ? msg.getPayload().toString() : "{}";

            // kafkaTemplate.send("lookup-events",
            //         msg.getAggregateType() + ":" + msg.getAggregateId(),
            //         payloadStr
            // ).get();
            log.info("Publishing outbox message id={} aggregateType={} aggregateId={} attempts={}",
                    msg.getId(), msg.getAggregateType(), msg.getAggregateId(), msg.getAttempts()
            );
            producer.send(
                    msg.getPayload(),
                    msg.getAggregateType() + ":" + msg.getAggregateId(),
                    msg.getAggregateType(),
                    msg.getAttempts()
            );

            msg.setStatus("PUBLISHED");
            msg.setPublished(true);
            msg.setPublishedAt(Instant.now());
            repository.save(msg);

        } catch (RuntimeException ex) {
            log.error("Runtime exception publishing message {}", msg.getId(), ex);
            handleFailure(msg, ex);
        }
        // preserve interrupt status if InterruptedException

    }

    private boolean readyForRetry(OutboxMessage msg) {
        if ("PUBLISHED".equals(msg.getStatus()) || "DLQ".equals(msg.getStatus())) {
            return false;
        }

        if ("PENDING".equals(msg.getStatus())) {
            return true;
        }

        long backoff = (long) Math.pow(2, msg.getAttempts()) * BASE_BACKOFF_MS;
        Instant nextTry = msg.getLastAttemptAt().plusMillis(backoff);
        return Instant.now().isAfter(nextTry);
    }

    @Transactional
    public void handleFailure(OutboxMessage msg, Exception ex) {
        msg.setAttempts(msg.getAttempts() + 1);
        msg.setLastAttemptAt(Instant.now());
        msg.setLastErrorMessage(ex.getMessage());

        log.error("Error handling failure for message {}", msg.getId(), ex);
        if (msg.getAttempts() >= MAX_RETRIES) {
            msg.setStatus("DLQ");
            msg.setDlqAt(Instant.now());
            log.error("Message {} moved to DLQ", msg.getId());
        } else {
            msg.setStatus("FAILED");
        }
        log.info("Updating outbox message {} status to {}", msg.getId(), msg.getStatus());
        repository.save(msg);
    }

}
