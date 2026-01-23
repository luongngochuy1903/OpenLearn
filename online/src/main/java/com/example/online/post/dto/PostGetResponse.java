package com.example.online.post.dto;

import com.example.online.course.dto.CourseGetResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostGetResponse {
    private Long postId;
    private String name;
    private String contentMarkdown;
    private LocalDateTime updateAt;
    private List<CourseGetResponse> courseGetResponses;
    private String creator;
    private Long creatorId;
    private List<String> contributors;
    private String communityName;
    private Long communityId;
}
