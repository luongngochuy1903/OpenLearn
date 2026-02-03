package com.example.online.post.service.impl;

import com.example.online.annotation.CheckCommunityMember;
import com.example.online.document.factory.DocumentGenerateFactory;
import com.example.online.document.service.DocumentService;
import com.example.online.domain.model.*;
import com.example.online.enumerate.*;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.exception.UnauthorizedException;
import com.example.online.post.dto.PostCreateRequest;
import com.example.online.post.enumerate.PostCreateType;
import com.example.online.post.service.PostCreateService;
import com.example.online.postcourse.service.PostCourseService;
import com.example.online.repository.CommunityRepository;
import com.example.online.repository.OutboxRepository;
import com.example.online.repository.PostRepository;
import com.example.online.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

@Service("PostWithoutCourseCreateServiceImpl")
@RequiredArgsConstructor
public class PostWithoutCourseCreateServiceImpl implements PostCreateService {
    private final UserService userService;
    private final PostRepository postRepository;
    private final PostCourseService postCourseService;
    private final DocumentGenerateFactory documentGenerateFactory;
    private final CommunityRepository communityRepository;
    private final OutboxRepository outboxRepository;

    @Override
    public PostCreateType getType() {
        return PostCreateType.WITHOUT_COURSE;
    }

    /*
    Function: Create Post without Course
     */
    @Override
    @Transactional
    public Post createPost(PostCreateRequest postCreateRequest, User authUser) {
        if (authUser == null) {
            throw new UnauthorizedException("You need to login first");
        }
        User user = userService.findUserById(authUser.getId());

        Post post = Post.builder()
                .name(postCreateRequest.getName())
                .contentMarkdown(postCreateRequest.getContentMarkdown())
                .createdAt(LocalDateTime.now())
                .creator(user)
                .documentURL(new ArrayList<>())
                .updateAt(LocalDateTime.now())
                .postCourses(new HashSet<>())
                .build();
        postRepository.save(post);

        PostCourse postCourse = PostCourse.builder()
                .user(user)
                .post(post)
                .role(ContributorRole.CREATOR)
                .build();
        postCourseService.save(postCourse);

        //Tạo document cho post
        if (postCreateRequest.getDocs() != null && !postCreateRequest.getDocs().isEmpty()) {
            DocumentService documentService = documentGenerateFactory.getService(DocumentOf.POST);
            for (var documentReq : postCreateRequest.getDocs()) {
                documentService.createDocument(post, documentReq);
            }
        }

        System.out.println("Test coi post có trường gì: " + post.getPostCourses().size());
        outboxRepository.save(OutBoxEvent.builder()
                .aggregateId(post.getId())
                .type(ESType.POST)
                .eventType(OutboxEventType.CHANGED)
                .status(OutboxStatus.NEW)
                .createdAt(Instant.now())
                .build());
        return post;
    }

    @Override
    @Transactional
    @CheckCommunityMember(communityIdParam = "communityId", userParam = "authUser")
    public Post createPost(Long communityId, PostCreateRequest postCreateRequest, User authUser) {
        if (authUser == null) {
            throw new UnauthorizedException("You need to login first");
        }
        User user = userService.findUserById(authUser.getId());

        Community community = communityRepository.findById(communityId).orElseThrow(() -> new ResourceNotFoundException("community not found"));

        Post post = Post.builder()
                .name(postCreateRequest.getName())
                .contentMarkdown(postCreateRequest.getContentMarkdown())
                .createdAt(LocalDateTime.now())
                .community(community)
                .creator(user)
                .documentURL(new ArrayList<>())
                .updateAt(LocalDateTime.now())
                .postCourses(new HashSet<>())
                .build();
        postRepository.save(post);

        PostCourse postCourse = PostCourse.builder()
                .user(user)
                .post(post)
                .role(ContributorRole.CREATOR)
                .build();
        postCourseService.save(postCourse);

        //Tạo document cho post
        if (postCreateRequest.getDocs() != null && !postCreateRequest.getDocs().isEmpty()) {
            DocumentService documentService = documentGenerateFactory.getService(DocumentOf.POST);
            for (var documentReq : postCreateRequest.getDocs()) {
                documentService.createDocument(post, documentReq);
            }
        }

        System.out.println("Test coi post có trường gì: " + post.getPostCourses().size());
        outboxRepository.save(OutBoxEvent.builder()
                .aggregateId(post.getId())
                .type(ESType.POST)
                .eventType(OutboxEventType.CHANGED)
                .status(OutboxStatus.NEW)
                .createdAt(Instant.now())
                .build());
        return post;
    }
}
