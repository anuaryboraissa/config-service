package com.softnet.config_service.components;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.domain.PageRequest;
// import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softnet.config_service.model.OutboxMessage;
import com.softnet.config_service.repository.OutboxRepository;

import org.springframework.beans.factory.annotation.Autowired;

@Component
public class OutboxPublisherScheduler {

    private final OutboxRepository outboxRepo;
    // private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public OutboxPublisherScheduler(OutboxRepository outboxRepo) {
        this.outboxRepo = outboxRepo;
    }

    @Scheduled(fixedDelayString = "${outbox.publish.delay.ms:2000}")
    public void publishPending() {
        List<OutboxMessage> pending = outboxRepo.findByPublishedFalseOrderByCreatedAtAsc(PageRequest.of(0, 50));
        for (OutboxMessage m : pending) {
            try {
                // topic selection, e.g. gepg.lookup.changed
                String topic = "gepg.lookup.changed";
                // kafkaTemplate.send(topic, m.getId().toString(), m.getPayload()).get(5, TimeUnit.SECONDS);
                m.setPublished(true);
                m.setPublishedAt(Instant.now());
                outboxRepo.save(m);
            } catch (Exception e) {
                // log and continue — do not mark published
            }
        }
    }
}
