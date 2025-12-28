package com.example.online.authentication.authenticate.service;

import com.example.online.authentication.authenticate.dto.AuthenticationRequest;
import com.example.online.authentication.authenticate.dto.AuthenticationResponse;
import com.example.online.authentication.authenticate.dto.RegisterRequest;
import com.example.online.authentication.authenticate.dto.RegisterResponse;

public interface AuthenticationService {
    AuthenticationResponse authentication(AuthenticationRequest request);
    RegisterResponse register(RegisterRequest registerRequest);
}
