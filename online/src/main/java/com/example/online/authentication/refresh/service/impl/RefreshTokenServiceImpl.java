package com.example.online.authentication.refresh.service.impl;

import com.example.online.authentication.authenticate.dto.AuthenticationResponse;
import com.example.online.authentication.jwt.service.JwtService;
import com.example.online.authentication.refresh.dto.RefreshTokenRequest;
import com.example.online.exception.BadRequestException;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.domain.model.RefreshToken;
import com.example.online.domain.model.User;
import com.example.online.exception.UnauthorizedException;
import com.example.online.repository.RefreshTokenRepository;
import com.example.online.authentication.refresh.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    public AuthenticationResponse generateRefreshToken(HttpServletRequest request, HttpServletResponse response){
        if (request.getCookies() == null){
            throw new BadRequestException("Something happened");
        }
        String refreshToken_cookie = null;
        for (Cookie cookie : request.getCookies()) {
            if ("refresh_token".equals(cookie.getName())) {
                refreshToken_cookie = cookie.getValue();
                break;
            }
        }

        if (refreshToken_cookie == null || refreshToken_cookie.isBlank()){
            throw new BadRequestException("Something happened");
        }

        RefreshToken refreshToken = findRefreshTokenByToken(refreshToken_cookie);

        verifyExpiration(refreshToken);

        var user = refreshToken.getUser();
        String newAccessToken = jwtService.generateToken(user);
        var newRefreshToken = rotateRefreshToken(user);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", newRefreshToken.getToken())
                .httpOnly(true)
//                .secure(true)
                .path("/api")
                .maxAge(7 * 24 * 60 * 60)
//                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return AuthenticationResponse.builder()
                .token(newAccessToken)
                .build();
    }

    //Generate new refresh token
    public RefreshToken createRefreshToken(User user){
        var refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60))
                .user(user)
                .expired(false)
                .revoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    // Check refresh token expired
    public RefreshToken verifyExpiration(RefreshToken refreshToken){
        if(refreshToken.getExpiryDate().isBefore(Instant.now())){
            refreshToken.setExpired(true);
            refreshTokenRepository.save(refreshToken);
            throw new UnauthorizedException("Session expired");
        }
        return refreshToken;
    }

    public RefreshToken findRefreshTokenByToken(String token){
        return refreshTokenRepository.findByToken(token).orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));
    }

    public void revokeUserTokens(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    @Transactional
    public RefreshToken rotateRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user); // xóa token cũ
        refreshTokenRepository.flush();
        return createRefreshToken(user);
    }
}
