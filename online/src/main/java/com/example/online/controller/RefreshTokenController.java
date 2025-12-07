package com.example.online.controller;

import com.example.online.DTO.AuthenticationResponse;
import com.example.online.DTO.RefreshTokenRequest;
import com.example.online.model.RefreshToken;
import com.example.online.service.JwtService;
import com.example.online.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshAccessToken(@RequestBody RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenService.findRefreshTokenByToken(request.getToken());

        refreshTokenService.verifyExpiration(refreshToken);

        var user = refreshToken.getUser();
        String newAccessToken = jwtService.generateToken(user);

        var newRefreshToken = refreshTokenService.rotateRefreshToken(user);

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationResponse);
    }

}
