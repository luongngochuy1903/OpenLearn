package com.example.online.domain.model;

import com.example.online.enumerate.ESType;
import com.example.online.enumerate.OutboxEventType;
import com.example.online.enumerate.OutboxStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter        // sinh getter cho tất cả field
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "outbox_events")
public class OutBoxEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private Long aggregateId;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private OutboxEventType eventType; // CHANGED/DELETED

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private ESType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private OutboxStatus status; // NEW/PROCESSING/RETRY/FAILED

//    private int retryCount;
//    private Instant nextRetryAt;
//
//    @Lob
//    private String lastError;

    private Instant createdAt;
    private Instant updatedAt;
}
