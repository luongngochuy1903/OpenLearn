package com.example.online.post.service.impl;

import com.example.online.course.dto.CourseGetResponse;
import com.example.online.course.service.CourseService;
import com.example.online.domain.model.Course;
import com.example.online.domain.model.Post;
import com.example.online.domain.model.User;
import com.example.online.enumerate.ContributorRole;
import com.example.online.exception.ForbiddenException;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.exception.UnauthorizedException;
import com.example.online.post.dto.PostCreateRequest;
import com.example.online.post.dto.PostGetResponse;
import com.example.online.post.dto.PostUpdateRequest;
import com.example.online.post.service.PostService;
import com.example.online.postcourse.service.PostCourseService;
import com.example.online.repository.PostRepository;
import com.example.online.user.service.UserService;
import com.example.online.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CourseService courseService;
    private final PostCourseService postCourseService;
    private final UserService userService;

    public void deletePost(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found !"));
        postRepository.delete(post);
        //Thêm xóa comment related
    }

    //Function: Modifying own post
    public Post updateMyPost(Long postId, PostUpdateRequest postUpdateRequest){
        var authUser = SecurityUtils.getCurrentUser();
        if (authUser == null) {
            throw new UnauthorizedException("You need to login first");
        }
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        User creator = postCourseService.getRoleOfPost(post, ContributorRole.CREATOR).get(0);
        if (!creator.getId().equals(authUser.getId())){
            throw new ForbiddenException("You don't have permission to modify this post");
        }

        if (postUpdateRequest.getName() != null){
            post.setName(postUpdateRequest.getName());
        }
        if(postUpdateRequest.getContentMarkdown() != null){
            post.setContentMarkdown(postUpdateRequest.getContentMarkdown());
        }
        post.setUpdateAt(LocalDateTime.now());
        return postRepository.save(post);
    }
}
