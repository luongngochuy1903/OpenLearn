package com.example.online.authentication.refresh.service.impl;

import com.example.online.exception.ResourceNotFoundException;
import com.example.online.domain.model.RefreshToken;
import com.example.online.domain.model.User;
import com.example.online.repository.RefreshTokenRepository;
import com.example.online.authentication.refresh.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

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

    public RefreshToken verifyExpiration(RefreshToken refreshToken){
        if(refreshToken.getExpiryDate().isBefore(Instant.now())){
            refreshToken.setExpired(true);
            refreshTokenRepository.save(refreshToken);
            throw new ResourceNotFoundException("Refresh token đã hết hạn. Vui lòng đăng nhập lại.");
        }
        return refreshToken;
    }

    public RefreshToken findRefreshTokenByToken(String token){
        return refreshTokenRepository.findByToken(token).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy refresh token này"));
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
