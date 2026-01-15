package com.example.online.annotation;

import com.example.online.domain.model.User;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckCourseCreator {
    String courseIdParam() default "courseId";
    String userParam() default "user";
}
