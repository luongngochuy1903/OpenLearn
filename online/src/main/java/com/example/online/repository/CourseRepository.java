package com.example.online.repository;

import com.example.online.domain.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByIdAndCreator_Id(Long courseId, Long userId);
}
