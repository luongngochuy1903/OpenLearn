package com.example.online.worker.outbox.service;

import com.example.online.domain.model.OutBoxEvent;
import com.example.online.enumerate.OutboxStatus;
import com.example.online.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxService {
    private final OutboxRepository outboxRepository;

    @Transactional
    public List<OutBoxEvent> claimBatch(int batchSize) {
        List<OutBoxEvent> jobs = outboxRepository.fetchBatchForUpdateSkipLocked(batchSize);
        if (jobs.isEmpty()) return List.of();

        //Mark the processing jobs in order to prevent inconsistency
        for (OutBoxEvent e : jobs) {
            e.setStatus(OutboxStatus.PROCESSING);
            e.setCreatedAt(Instant.now());
        }

        // optional, but clear
        outboxRepository.saveAll(jobs);
        return jobs;
    }

    @Transactional
    public void finalizeBatch(List<Long> doneIds, List<Long> failedIds) {
        if (failedIds != null && !failedIds.isEmpty()) {

            List<OutBoxEvent> events = outboxRepository.findAllById(failedIds);

            for (OutBoxEvent e : events) {
                e.setStatus(OutboxStatus.FAILED);
                e.setCreatedAt(Instant.now());
            }
            outboxRepository.saveAll(events);
        }

        if (doneIds != null && !doneIds.isEmpty()) {

            List<OutBoxEvent> events2 = outboxRepository.findAllById(doneIds);

            for (OutBoxEvent e : events2) {
                e.setStatus(OutboxStatus.FAILED);
                e.setCreatedAt(Instant.now());
            }
            outboxRepository.deleteAll(events2);
        }
    }
}
