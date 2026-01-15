package com.example.online.postcourse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateResponse {
    private Long postId;
    private String message;
    private String url;
}
