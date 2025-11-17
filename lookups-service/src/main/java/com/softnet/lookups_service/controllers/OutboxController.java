package com.softnet.lookups_service.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.softnet.lookups_service.model.OutboxMessage;
import com.softnet.lookups_service.repository.OutboxRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/outbox")
@RequiredArgsConstructor
public class OutboxController {

    private final OutboxRepository outboxRepository;

    private static final int DEFAULT_BATCH_SIZE = 100;
    private static final int MAX_RETRIES = 20;

    // -------------------------
    // List pending messages
    // -------------------------
    @GetMapping("/pending")
    public List<OutboxMessage> getPending(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "100") int size) {
        return outboxRepository.findPendingMessages(PageRequest.of(page, size));
    }

    // -------------------------
    // List retryable messages
    // -------------------------
    @GetMapping("/retryable")
    public List<OutboxMessage> getRetryable(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "100") int size) {
        return outboxRepository.findRetryableMessages(MAX_RETRIES, PageRequest.of(page, size));
    }

    // -------------------------
    // List all messages ready for publishing (pending + retryable)
    // -------------------------
    @GetMapping("/ready")
    public List<OutboxMessage> getReady(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "100") int size) {
        return outboxRepository.findPendingAndRetryable(MAX_RETRIES, PageRequest.of(page, size));
    }

    // -------------------------
    // List DLQ messages
    // -------------------------
    @GetMapping("/dlq")
    public List<OutboxMessage> getDLQ(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "100") int size) {
        return outboxRepository.findByStatusOrderByDlqAtAsc("DLQ");
    }

    // -------------------------
    // Replay a DLQ message
    // -------------------------
    @PostMapping("/dlq/{id}/replay")
    public OutboxMessage replayDLQ(@PathVariable UUID id, @RequestParam(required = false, defaultValue = "system") String replayedBy) {
        OutboxMessage msg = outboxRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DLQ message not found"));

        // Reset for replay
        msg.setStatus("PENDING");
        msg.setAttempts(0);
        msg.setDlqAt(null);
        msg.setLastAttemptAt(null);
        outboxRepository.save(msg);

        return msg;
    }

    // -------------------------
    // Resolve a DLQ message without replaying
    // -------------------------
    @PostMapping("/dlq/{id}/resolve")
    public OutboxMessage resolveDLQ(@PathVariable UUID id,
                                    @RequestParam(required = false, defaultValue = "system") String resolvedBy,
                                    @RequestParam(required = false) String notes) {
        OutboxMessage msg = outboxRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DLQ message not found"));

        msg.setStatus("RESOLVED");
        msg.setDlqAt(null);
        msg.setAttempts(0);
        // Store audit info
        msg.setResolvedBy(resolvedBy);
        msg.setResolvedAt(java.time.Instant.now());
        msg.setResolutionNotes(notes);

        outboxRepository.save(msg);

        return msg;
    }
}
