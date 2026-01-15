package com.example.online.domain.model;

import com.example.online.enumerate.CommunityStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter        // sinh getter cho tất cả field
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "request_community")
public class RequestJoiningCommunity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private Community community;

    @Enumerated(EnumType.STRING)
    private CommunityStatus status;

    private LocalDateTime createdAt;
}
