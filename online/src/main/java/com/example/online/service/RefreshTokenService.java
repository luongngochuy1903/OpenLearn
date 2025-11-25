package com.example.online.service;

import com.example.online.model.RefreshToken;
import com.example.online.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenService {
    RefreshToken rotateRefreshToken(User user);
    void revokeUserTokens(User user);
    RefreshToken findRefreshTokenByToken(String token);
    RefreshToken createRefreshToken(User user);
    RefreshToken verifyExpiration(RefreshToken refreshToken);
}
