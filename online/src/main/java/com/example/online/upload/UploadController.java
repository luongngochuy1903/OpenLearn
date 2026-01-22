package com.example.online.upload;

import com.example.online.post.controller.PostController;
import com.example.online.upload.dto.PresignedURLRequest;
import com.example.online.upload.dto.PresignedURLResponse;
import com.example.online.upload.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
public class UploadController {
    private final UploadService uploadService;
    private static final Logger LOG = LoggerFactory.getLogger(UploadController.class);

    @PostMapping
    public ResponseEntity<PresignedURLResponse> getPresignedURL(@RequestBody PresignedURLRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadService.generatePresignedURL(request));
    }

}
