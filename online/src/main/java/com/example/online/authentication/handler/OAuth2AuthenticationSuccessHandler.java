package com.example.online.authentication.handler;

import com.example.online.authentication.authenticate.dto.AuthenticationResponse;
import com.example.online.authentication.authenticate.service.OAuth2Service;
import com.example.online.authentication.jwt.service.JwtService;
import com.example.online.authentication.refresh.service.RefreshTokenService;
import com.example.online.document.service.impl.LessonDocumentServiceImpl;
import com.example.online.domain.model.User;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final OAuth2Service oAuth2Service;
    private final UserRepository userRepository;
    private static final Logger LOG = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        User user = userRepository.findByEmail(oAuth2User.getAttribute("email"))
                .orElseThrow(() -> new ResourceNotFoundException("Email not found in handler"));

        String code = oAuth2Service.generateAuthorizationCode(user);
//        var jwt = jwtService.generateToken(user);
//        var refreshToken = refreshTokenService.rotateRefreshToken(user);

//        ResponseCookie cookie = ResponseCookie.from("access_token", jwt)
//                .httpOnly(true)
////                .secure(true)
//                .path("/")
//                .maxAge(7 * 24 * 60 * 60)
////                .sameSite("None")
//                .build();
//
//        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken.getToken())
//                .httpOnly(true)
////                .secure(true)
//                .path("/api")
//                .maxAge(30L * 24 * 60 * 60)
////                .sameSite("None")
//                .build();

//        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        String redirectUrl = UriComponentsBuilder
                .fromUriString("http://localhost:3000/oauth2/success")
                .queryParam("success", "true")
                .queryParam("message", "Login success")
                .queryParam("code", code)
                .build()
                .toUriString();
        LOG.info("Handler thành công chuẩn bị redirect về http://localhost:3000/oauth2/success");
        // redirect về FE
        response.sendRedirect(redirectUrl);
    }
}
