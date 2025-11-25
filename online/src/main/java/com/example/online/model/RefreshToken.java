package com.example.online.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "userId")
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    private boolean revoked;

    private boolean expired;

    @Column(nullable = false)
    private Instant expiryDate;
}
