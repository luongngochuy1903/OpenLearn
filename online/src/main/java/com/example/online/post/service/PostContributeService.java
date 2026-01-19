package com.example.online.post.service;

import com.example.online.domain.model.User;
import com.example.online.post.dto.RequestAttachResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostContributeService {
    Page<RequestAttachResponse> getRequestAttach(Long postId, User user, Pageable pageable);
    void approveCourseToPost(Long postId, Long courseId, User user, Long courseCreatorId);
    void declineCourseToPost(Long postId, Long courseId, String reason, User user);
    void removeCourseFromPost(Long postId, Long courseId, User user);
    void requestCourseToPost(Long postId, Long courseId, User user);
    void banThisUserToContribute(Long postId, Long targetManId, User user);
    void removeBan(Long postId, Long targetManId, User user);

}
