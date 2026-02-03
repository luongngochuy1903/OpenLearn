package com.example.online.authentication.authenticate.service.impl;
import com.example.online.authentication.authenticate.service.AuthenticationService;

import com.example.online.authentication.authenticate.dto.AuthenticationRequest;
import com.example.online.authentication.authenticate.dto.AuthenticationResponse;
import com.example.online.authentication.authenticate.dto.RegisterRequest;
import com.example.online.authentication.authenticate.dto.RegisterResponse;
import com.example.online.document.factory.DocumentGenerateFactory;
import com.example.online.document.service.DocumentService;
import com.example.online.domain.model.OnetimeToken;
import com.example.online.enumerate.DocumentOf;
import com.example.online.enumerate.Role;
import com.example.online.exception.BadRequestException;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.domain.model.User;
import com.example.online.exception.UnauthorizedException;
import com.example.online.helper.Sha256Hashing;
import com.example.online.repository.OnetimeTokenRepository;
import com.example.online.repository.UserRepository;
import com.example.online.authentication.jwt.service.JwtService;
import com.example.online.authentication.refresh.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final DocumentGenerateFactory documentGenerateFactory;
    private final OnetimeTokenRepository onetimeTokenRepository;
    private final JwtService jwtService;
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    //Hàm đăng nhập rồi trả về token
    @Transactional
    public AuthenticationResponse authentication(AuthenticationRequest request, HttpServletResponse response){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid email or password");
        }

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ResourceNotFoundException("You don't have an account"));
        var jwt = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.rotateRefreshToken(user);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken.getToken())
                .httpOnly(true)
//                .secure(true)
                .path("/api")
                .maxAge(30L * 24 * 60 * 60)
//                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        LOG.info("User {} authenticate for email {}", user.getLastName() + " " + user.getFirstName(), user.getEmail());
        return AuthenticationResponse.builder()
                .token(jwt)
                .redirectURL("/api/v1/home")
                .build();
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        var user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .documentURL(new ArrayList<>())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        if (registerRequest.getDocs() != null && !registerRequest.getDocs().isEmpty()) {
            DocumentService documentService = documentGenerateFactory.getService(DocumentOf.USER);
            for (var documentReq : registerRequest.getDocs()) {
                documentService.createDocument(user, documentReq);
            }
        }
        LOG.info("User {} register with info: fullName {}, role: {}", user.getEmail(), user.getLastName() + " " + user.getFirstName(), user.getRole());
        return RegisterResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }

    /*
        Function: After OAuth2 Authorization succeeded, BE return authorization code to FE in redirect form
        and FE call POST this endpoint to get access token, refresh token
     */
    public AuthenticationResponse exchangeAuthorizationCode(AuthenticationRequest request, HttpServletResponse response, String code){
        if (code == null || code.isBlank()) {
            throw new BadRequestException("Missing authorization code");
        }

        String hashedCode = Sha256Hashing.sha256(code);
        OnetimeToken onetimeToken = onetimeTokenRepository.findByCodeHash(hashedCode)
                .orElseThrow(() -> new ResourceNotFoundException("Authorization code not found"));
        if (onetimeToken.getExpiredAt().isBefore(Instant.now())){
            throw new BadRequestException("Authorization code expired. Please login again");
        }

        User user = onetimeToken.getUser();
        var jwt = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.rotateRefreshToken(user);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken.getToken())
                .httpOnly(true)
//                .secure(true)
                .path("/api")
                .maxAge(30L * 24 * 60 * 60)
//                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        onetimeTokenRepository.delete(onetimeToken);

        LOG.info("User {} authenticate for email {} with OAuth2 authorization", user.getLastName() + " " + user.getFirstName(), user.getEmail());
        return AuthenticationResponse.builder()
                .token(jwt)
                .redirectURL("/api/v1/home")
                .build();
    }
}
