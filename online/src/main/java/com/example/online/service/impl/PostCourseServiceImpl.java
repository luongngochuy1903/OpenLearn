package com.example.online.service.impl;

import com.example.online.DTO.PostCourseRequest;
import com.example.online.model.PostCourse;
import com.example.online.repository.PostCourseRepository;
import com.example.online.service.PostCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCourseServiceImpl implements PostCourseService {
    private final PostCourseRepository postCourseRepository;
    public PostCourse createPostCourse(PostCourseRequest postCourseRequest){
        return null;
    }

    public void save(PostCourse postCourse){
        postCourseRepository.save(postCourse);
    }
}
