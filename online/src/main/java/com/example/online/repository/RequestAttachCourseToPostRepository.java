package com.example.online.repository;

import com.example.online.domain.model.Course;
import com.example.online.domain.model.Post;
import com.example.online.domain.model.RequestAttachCourseToPost;
import com.example.online.domain.model.User;
import com.example.online.enumerate.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequestAttachCourseToPostRepository extends JpaRepository<RequestAttachCourseToPost, Long> {
    Optional<RequestAttachCourseToPost> findByPostAndCourse(Post post, Course course);
    void deleteByPostAndCourse(Post post, Course course);
    Optional<RequestAttachCourseToPost> findByUser(User user);
    Optional<RequestAttachCourseToPost> findByPost_IdAndCourse_Id(Long postId, Long courseId);
    Optional<RequestAttachCourseToPost> findByPost_IdAndCourse_IdAndStatus(Long postId, Long courseId, RequestStatus status);
}
