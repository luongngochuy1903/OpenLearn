package com.example.online.repository;

import com.example.online.domain.model.Course;
import com.example.online.enumerate.ContributorRole;
import com.example.online.domain.model.Post;
import com.example.online.domain.model.PostCourse;
import com.example.online.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostCourseRepository extends JpaRepository<PostCourse, Long> {
    List<PostCourse> findByPostAndRole(Post post, ContributorRole role);
    List<PostCourse> findByPost(Post post);
    List<PostCourse> findByUser(User user);
    Optional<PostCourse> findByPost_IdAndCourse_Id(Long postId, Long courseId);
    boolean existsByPost_IdAndCourse_Id(Long postId, Long courseId);
}
