package com.example.online.service;

import com.example.online.DTO.AuthenticationRequest;
import com.example.online.DTO.AuthenticationResponse;
import com.example.online.DTO.RegisterRequest;
import com.example.online.DTO.RegisterResponse;

public interface AuthenticationService {
    AuthenticationResponse authentication(AuthenticationRequest request);
    RegisterResponse register(RegisterRequest registerRequest);
}
