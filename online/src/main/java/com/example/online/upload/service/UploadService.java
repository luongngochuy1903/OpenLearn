package com.example.online.upload.service;

import com.example.online.domain.model.User;
import com.example.online.upload.dto.PresignedURLRequest;
import com.example.online.upload.dto.PresignedURLResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.UUID;

public abstract class UploadService {

    public abstract PresignedURLResponse generatePresignedURL(PresignedURLRequest presignedURLRequest);
    public abstract String generatePresignedViewUrl(String objectKey);

    public String generateKey(String fileName){
        return UUID.randomUUID() + fileName;
    }

}
