package com.example.online.repository;

import com.example.online.enumerate.ContributorRole;
import com.example.online.domain.model.Course;
import com.example.online.domain.model.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseModuleRepository extends JpaRepository<CourseModule, Long> {
    List<CourseModule> findUsersByCourseAndRole(Course course, ContributorRole role);
    List<CourseModule> findModulesByCourse(Course course);
}
