package com.example.online.DTO;

import com.example.online.model.Tag;
import com.example.online.repository.TagRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateWithCourseRequest {
    private String name;
    private String contentUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    private List<CourseCreateRequest> courseCreateRequests;


}
