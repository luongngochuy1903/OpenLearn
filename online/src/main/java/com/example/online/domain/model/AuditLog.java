package com.example.online.domain.model;

import com.example.online.enumerate.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long actorId;
    private String actorUsername;

    @Enumerated(EnumType.STRING)
    private Role actorRole;

    private String action;
    private String description;

    private String resourceType;
    private String methodName;

//    private String status;
//
//    @Column(columnDefinition = "TEXT")
//    private String errorMessage;

//    @Column(columnDefinition = "TEXT")
//    private String oldData;
//
//    @Column(columnDefinition = "TEXT")
//    private String newData;

    private String endpoint;
    private String httpMethod;

    private String ipAddress;

    private LocalDateTime createdAt;
}

