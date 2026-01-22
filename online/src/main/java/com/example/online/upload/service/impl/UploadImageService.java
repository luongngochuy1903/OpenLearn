package com.example.online.upload.service.impl;

import com.example.online.upload.dto.PresignedURLRequest;
import com.example.online.upload.dto.PresignedURLResponse;
import com.example.online.upload.helper.FileTypeDetector;
import com.example.online.upload.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Base64;

import static com.example.online.upload.helper.FileTypeDetector.FileType.UNKNOWN;

@Service
@RequiredArgsConstructor
public class UploadImageService extends UploadService {
    private final S3Presigner s3Presigner;
    private final String bucket = "openlearnbucket";
    private static final Logger LOG = LoggerFactory.getLogger(UploadImageService.class);

    public PresignedURLResponse generatePresignedURL(PresignedURLRequest presignedURLRequest){
        byte[] headerBytes = Base64.getDecoder().decode(presignedURLRequest.getFileHeaderBase64());

        if (headerBytes.length < 512) {
            throw new IllegalArgumentException("Invalid file header");
        }

        FileTypeDetector.FileType type =
                FileTypeDetector.detect(headerBytes);

        if (type == UNKNOWN) {
            throw new IllegalArgumentException("Unsupported file type");
        }

        LOG.info("Content-Type: {}", presignedURLRequest.getContentType());
        String objectKey = generateKey(presignedURLRequest.getFileName());
        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucket)
                .contentType(presignedURLRequest.getContentType()).key(objectKey).build();

        PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofMinutes(4))
                .build();

        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(putObjectPresignRequest);

        return PresignedURLResponse.builder()
                .presignedUrl(presignedPutObjectRequest.url().toString())
                .expiresAt(presignedPutObjectRequest.expiration())
                .objectKey(objectKey).build();
    }

    public String generatePresignedViewUrl(String objectKey) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .getObjectRequest(getObjectRequest)
                        .signatureDuration(Duration.ofMinutes(3))
                        .build();

        PresignedGetObjectRequest presigned =
                s3Presigner.presignGetObject(presignRequest);

        return presigned.url().toString();
    }
}
