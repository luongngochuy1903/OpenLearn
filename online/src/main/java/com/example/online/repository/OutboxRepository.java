package com.example.online.repository;

import com.example.online.domain.model.OutBoxEvent;
import com.example.online.enumerate.ESType;
import com.example.online.enumerate.OutboxEventType;
import com.example.online.enumerate.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OutboxRepository extends JpaRepository<OutBoxEvent, Long> {
    @Query(value = """
            SELECT *
             FROM outbox_events o
             WHERE o.status IN ('NEW', 'FAILED')
             ORDER BY
               CASE WHEN o.status = 'NEW' THEN 0 ELSE 1 END,
               o.created_at ASC
             LIMIT :limit
             FOR UPDATE SKIP LOCKED;
            """, nativeQuery = true)
    List<OutBoxEvent> fetchBatchForUpdateSkipLocked(@Param("limit") int limit);


    Optional<OutBoxEvent> findByAggregateIdAndTypeAndStatusIn(Long aggregateId, ESType type, List<OutboxStatus> status);
}
