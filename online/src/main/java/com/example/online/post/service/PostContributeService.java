package com.example.online.post.service;

import com.example.online.domain.model.User;

public interface PostContributeService {
    void approveCourseToPost(Long postId, Long courseId, User user);
    void declineCourseToPost(Long postId, Long courseId, String reason, User user);
    void removeCourseFromPost(Long postId, Long courseId, User user);
    void requestCourseToPost(Long postId, Long courseId, User user);

}
