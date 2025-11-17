package com.softnet.lookups_service.services;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LookupProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "lookups-events";
    private static final Logger log = LoggerFactory.getLogger(LookupProducer.class);

    public void send(Object event, String key, String lookupCategory, int retryCount) {
        // kafkaTemplate.send(TOPIC, event.getOrderId(), event);
      log.info("Sending event to topic {} with key={} lookupCategory={} retryCount={}",
              TOPIC, key, lookupCategory, retryCount);
        ProducerRecord<String, Object> record = new ProducerRecord<>(TOPIC, key, event);
        // Add headers
        record.headers().add(new RecordHeader("retry-count", Integer.toString(retryCount).getBytes()));
        record.headers().add("lookupCategory", lookupCategory.getBytes(StandardCharsets.UTF_8));

        log.info("Producing record to topic {} with key={} headers={}",
                TOPIC, key, record.headers());
        kafkaTemplate.send(record).whenComplete((result, ex) -> {
    if (ex != null) {
        log.error("❌ Kafka send failed for key={} error={}", key, ex.getMessage(), ex);
    } else {
        log.info("✔ Kafka send success: {}", result.getRecordMetadata());
    }
});
    }
}


//how consumer will know which category it is consuming

// @KafkaListener(
//     topics = "lookup-events",
//     groupId = "lookups-service-group"
// )
// public void consumeLookup(ConsumerRecord<String, LookupValueEvent> record) {
//     LookupValueEvent event = record.value();
    
//     if (!"CURRENCY".equals(event.getLookupCategory())) return; // skip irrelevant messages
    
//     processCurrencyLookup(event);
// }

