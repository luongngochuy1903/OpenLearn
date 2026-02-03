package com.example.online.worker;

import com.example.online.domain.model.OutBoxEvent;
import com.example.online.elasticsearch.helper.BulkResult;
import com.example.online.enumerate.ESType;
import com.example.online.enumerate.OutboxEventType;
import com.example.online.enumerate.OutboxStatus;
import com.example.online.repository.OutboxRepository;
import com.example.online.worker.outbox.service.OutboxService;
import com.example.online.worker.service.CourseIndexService;
import com.example.online.worker.service.PostIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OutboxWorker {
    private final CourseIndexService courseIndexService;
    private final PostIndexService postIndexService;
    private final OutboxRepository outboxRepository;
    private final OutboxService outboxService;

    record Key(ESType type, OutboxEventType eventType) {}

    @Scheduled(fixedDelay = 2000)
    void run() {
        // Fix trường hợp trong 500ms có hơn 20 new BatchSize => lạc record (sửa query)
        int batchSize = 40;
        List<Long> idDrops = new ArrayList<>();
        List<Long> idFailed = new ArrayList<>();

        //Fetch batch job (involving NEW and FAILED)
        List<OutBoxEvent> getProcessingJobIds = outboxService.claimBatch(batchSize);
        if (getProcessingJobIds.isEmpty()) return;

        //Divided by COURSE_CHANGED/COURSE_DELETED/POST_CHANGED/POST_DELETED
        Map<Key, List<OutBoxEvent>> groups = getProcessingJobIds.stream()
                .collect(Collectors.groupingBy(e -> new Key(e.getType(), e.getEventType())));

        // Streaming each group
        for (Map.Entry<Key, List<OutBoxEvent>> entry : groups.entrySet()){
            Key key = entry.getKey();
            List<OutBoxEvent> events = entry.getValue();

            // Get OutBoxEvent id by Aggregate id
            Map<Long, Long> outboxIdsByAggId = events.stream()
                    .collect(Collectors.toMap(
                            OutBoxEvent::getAggregateId,
                            OutBoxEvent::getId
                    ));

            //Get all aggregateId in this key so we can batch indexing
            List<Long> aggregateIds = outboxIdsByAggId.keySet().stream().toList();

            //Start batch indexing and get success ids, failed ids
            BulkResult r = dispatch(key, aggregateIds);
            for (String id : r.successIds()) {
                Long aggId = Long.parseLong(id);
                idDrops.add(outboxIdsByAggId.get(aggId));
            }
            for (String id : r.failedIds()) {
                Long aggId = Long.parseLong(id);
                idFailed.add(outboxIdsByAggId.get(aggId));
            }
        }
        outboxService.finalizeBatch(idDrops, idFailed);
    }

    public OutBoxEvent getOutBoxEvent(Long aggregateId, ESType type, List<OutboxStatus> status){
        return outboxRepository
                .findByAggregateIdAndTypeAndStatusIn(aggregateId, type, status).orElse(null);
    }

    private BulkResult dispatch(Key key, List<Long> aggregateIds){
        return switch(key.type()){
            case POST -> switch (key.eventType()){
                case CHANGED -> postIndexService.indexPost(aggregateIds);
                case DELETED -> postIndexService.dropPost(aggregateIds);
            };
            case COURSE -> switch (key.eventType()){
                case CHANGED -> courseIndexService.indexCourse(aggregateIds);
                case DELETED -> courseIndexService.dropCourse(aggregateIds);
            };
        };
    }
}

