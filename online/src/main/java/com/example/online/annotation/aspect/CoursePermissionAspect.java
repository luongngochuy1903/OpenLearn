package com.example.online.annotation.aspect;

import com.example.online.annotation.CheckCourseCreator;
import com.example.online.domain.model.Course;
import com.example.online.domain.model.User;
import com.example.online.exception.ForbiddenException;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class CoursePermissionAspect {

    private final CourseRepository courseRepository;
    private static final Logger LOG = LoggerFactory.getLogger(CoursePermissionAspect.class);

    @Around("@annotation(checkCourseCreator)")
    public Object checkCreator(
            ProceedingJoinPoint joinPoint,
            CheckCourseCreator checkCourseCreator
    ) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        LOG.info("Course Param names: {}", Arrays.toString(paramNames));
        Object[] args = joinPoint.getArgs();

        Long courseId = null;
        Object userObj = null;

        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals(checkCourseCreator.courseIdParam())) {
                courseId = (Long) args[i];
            }
            if (paramNames[i].equals(checkCourseCreator.userParam())) {
                userObj = args[i];
            }
        }

        if (courseId == null) {
            throw new IllegalStateException("CourseId parameter not found");
        }
        if (userObj == null) {
            throw new IllegalStateException("User parameter not found");
        }

        if (!(userObj instanceof User user)) {
            throw new IllegalStateException("User parameter must be of type User");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!course.getCreator().getId().equals(user.getId())) {
            throw new ForbiddenException("You don't have permission to modify this course");
        }
        return joinPoint.proceed();
    }
}
