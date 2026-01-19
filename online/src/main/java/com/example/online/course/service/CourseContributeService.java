package com.example.online.course.service;

import com.example.online.course.dto.CourseRequestAttachResponse;
import com.example.online.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseContributeService {
    Page<CourseRequestAttachResponse> getRequestAttach(Long courseId, User user, Pageable pageable);
    void approveModuleToCourse(Long courseId, Long moduleId, User user, Long moduleCreatorId);
    void declineModuleToCourse(Long courseId, Long moduleId, String reason, User user);
    void banThisUserToContribute(Long courseId, Long targetManId, User user);
    void removeBan(Long courseId, Long targetManId, User user);
    void removeModuleFromCourse(Long courseId, Long moduleId, User user);
    void requestModuleToCourse(Long courseId, Long moduleId, User user);

}
