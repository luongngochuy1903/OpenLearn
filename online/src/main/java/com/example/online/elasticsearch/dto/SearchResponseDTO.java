package com.example.online.elasticsearch.dto;

import com.example.online.course.dto.CourseGetResponse;
import com.example.online.post.dto.PostGetResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponseDTO {
    private List<PostGetResponse> postGetResponseList;
    private List<CourseGetResponse> courseGetResponseList;
}
