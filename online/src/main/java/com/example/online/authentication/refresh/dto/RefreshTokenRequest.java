package com.example.online.authentication.refresh.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class RefreshTokenRequest {
    private String token;
}
