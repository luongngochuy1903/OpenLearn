package com.example.online.authentication.refresh.service;

import com.example.online.domain.model.RefreshToken;
import com.example.online.domain.model.User;

public interface RefreshTokenService {
    RefreshToken rotateRefreshToken(User user);
    void revokeUserTokens(User user);
    RefreshToken findRefreshTokenByToken(String token);
    RefreshToken createRefreshToken(User user);
    RefreshToken verifyExpiration(RefreshToken refreshToken);
}
