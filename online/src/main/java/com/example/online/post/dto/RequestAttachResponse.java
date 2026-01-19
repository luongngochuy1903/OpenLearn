package com.example.online.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestAttachResponse {
    private Long courseId;
    private String courseName;
    private String courseURL;

    private Long creatorId;
    private String creatorName;
    private String avatarURL;
}
