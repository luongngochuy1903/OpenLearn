package com.example.online.repository;

import com.example.online.enumerate.ContributorRole;
import com.example.online.domain.model.Post;
import com.example.online.domain.model.PostCourse;
import com.example.online.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCourseRepository extends JpaRepository<PostCourse, Long> {
    List<PostCourse> findUsersByPostAndRole(Post post, ContributorRole role);
    List<PostCourse> findCoursesByPost(Post post);
    List<PostCourse> findPostsByUser(User user);
}
