package com.example.online.authentication.refresh.controller;

import com.example.online.authentication.authenticate.dto.AuthenticationResponse;
import com.example.online.authentication.refresh.dto.RefreshTokenRequest;
import com.example.online.domain.model.RefreshToken;
import com.example.online.authentication.jwt.service.JwtService;
import com.example.online.authentication.refresh.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<AuthenticationResponse> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        AuthenticationResponse authenticationResponse = refreshTokenService.generateRefreshToken(request, response);
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationResponse);
    }

}
