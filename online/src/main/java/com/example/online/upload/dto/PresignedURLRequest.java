package com.example.online.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresignedURLRequest {
    private String contentType;
    private String fileName;
    private String fileHeaderBase64; //4KB đầu
}
