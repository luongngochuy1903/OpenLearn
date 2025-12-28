package com.example.online.authentication.authenticate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AuthenticationResponse {
    private String token;
    private String redirectURL;
    private String refreshToken;
}
