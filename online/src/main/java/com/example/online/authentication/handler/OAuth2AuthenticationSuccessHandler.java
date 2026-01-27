package com.example.online.authentication.handler;

import com.example.online.authentication.authenticate.dto.AuthenticationResponse;
import com.example.online.authentication.jwt.service.JwtService;
import com.example.online.authentication.refresh.service.RefreshTokenService;
import com.example.online.domain.model.User;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        User user = userRepository.findByEmail(oAuth2User.getAttribute("email"))
                .orElseThrow(() -> new ResourceNotFoundException("Email not found in handler"));
        var jwt = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.rotateRefreshToken(user);

        AuthenticationResponse authResponse =
                AuthenticationResponse.builder()
                        .token(jwt)
                        .refreshToken(refreshToken.getToken())
                        .redirectURL("/api/v1/home")
                        .build();

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getOutputStream(), authResponse);
    }
}
