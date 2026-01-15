package com.example.online.domain.model;

import com.example.online.enumerate.BanTarget;
import com.example.online.enumerate.BanType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ban_action")
public class ActionBan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private BanType action;

    private Long targetId;

    private LocalDateTime bannedAt;
}
