package com.example.online.authentication.authenticate.service;

import com.example.online.authentication.authenticate.dto.AuthenticationRequest;
import com.example.online.authentication.authenticate.dto.AuthenticationResponse;
import com.example.online.authentication.authenticate.dto.RegisterRequest;
import com.example.online.authentication.authenticate.dto.RegisterResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {
    AuthenticationResponse authentication(AuthenticationRequest request, HttpServletResponse response);
    RegisterResponse register(RegisterRequest registerRequest);
    AuthenticationResponse exchangeAuthorizationCode(AuthenticationRequest request, HttpServletResponse response, String code);
}
