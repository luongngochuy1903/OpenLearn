package com.example.online.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresignedURLResponse {
    private String presignedUrl;
    private Instant expiresAt;
    private String objectKey;
}
