package com.example.online.service.impl;

import com.example.online.model.CourseModule;
import com.example.online.repository.CourseModuleRepository;
import com.example.online.service.CourseModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseModuleServiceImpl implements CourseModuleService {
    private final CourseModuleRepository courseModuleRepository;
    public void save (CourseModule courseModule){
        courseModuleRepository.save(courseModule);
    }
}
