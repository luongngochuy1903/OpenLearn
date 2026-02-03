package com.example.online.authentication.refresh.service;

import com.example.online.authentication.authenticate.dto.AuthenticationResponse;
import com.example.online.authentication.refresh.dto.RefreshTokenRequest;
import com.example.online.domain.model.RefreshToken;
import com.example.online.domain.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface RefreshTokenService {
    RefreshToken rotateRefreshToken(User user);
    void revokeUserTokens(User user);
    RefreshToken findRefreshTokenByToken(String token);
    RefreshToken createRefreshToken(User user);
    RefreshToken verifyExpiration(RefreshToken refreshToken);
    AuthenticationResponse generateRefreshToken(HttpServletRequest request, HttpServletResponse response);
}
