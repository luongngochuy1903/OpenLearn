package com.example.online.course.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseRequestAttachResponse {
    private Long moduleId;
    private String moduleName;
    private String moduleURL;

    private Long creatorId;
    private String creatorName;
    private String avatarURL;
}
