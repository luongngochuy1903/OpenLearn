package com.example.online.authentication.authenticate.service.impl;

import com.example.online.authentication.authenticate.controller.AuthenticationController;
import com.example.online.authentication.authenticate.service.AuthenticationService;

import com.example.online.authentication.authenticate.dto.AuthenticationRequest;
import com.example.online.authentication.authenticate.dto.AuthenticationResponse;
import com.example.online.authentication.authenticate.dto.RegisterRequest;
import com.example.online.authentication.authenticate.dto.RegisterResponse;
import com.example.online.elasticsearch.service.IndexService;
import com.example.online.enumerate.Role;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.domain.model.User;
import com.example.online.repository.UserRepository;
import com.example.online.authentication.jwt.service.JwtService;
import com.example.online.authentication.refresh.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    //Hàm đăng nhập rồi trả về token
    @Transactional
    public AuthenticationResponse authentication(AuthenticationRequest request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var jwt = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.rotateRefreshToken(user);

        LOG.info("User {} authenticate for email {}", user.getLastName() + " " + user.getFirstName(), user.getEmail());
        return AuthenticationResponse.builder()
                .token(jwt)
                .redirectURL("/api/v1/home")
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        var user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        LOG.info("User {} register with info: fullName {}, role: {}", user.getEmail(), user.getLastName() + " " + user.getFirstName(), user.getRole());
        return RegisterResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}
