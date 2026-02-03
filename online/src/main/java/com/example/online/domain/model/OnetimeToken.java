package com.example.online.domain.model;

import com.example.online.enumerate.OneTimeTokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "onetime_token")
public class OnetimeToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OneTimeTokenType type;

    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private Instant expiredAt;
}
