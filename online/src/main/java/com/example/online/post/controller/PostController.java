package com.example.online.post.controller;

import com.example.online.annotation.CurrentUser;
import com.example.online.domain.model.Post;
import com.example.online.domain.model.User;
import com.example.online.post.dto.PostCreateRequest;
import com.example.online.post.dto.PostGetResponse;
import com.example.online.post.dto.PostUpdateRequest;
import com.example.online.post.dto.RequestAttachResponse;
import com.example.online.post.enumerate.PostCreateType;
import com.example.online.post.factory.PostCreateFactory;
import com.example.online.post.service.PostContributeService;
import com.example.online.post.service.PostQueryService;
import com.example.online.post.service.PostService;
import com.example.online.post.service.impl.PostContributeServiceImpl;
import com.example.online.postcourse.dto.PostCreateResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostQueryService postQueryService;
    private final PostService postService;
    private final PostCreateFactory postCreateFactory;
    private final PostContributeService postContributeService;
    private static final Logger LOG = LoggerFactory.getLogger(PostController.class);

    @PostMapping("/{types}")
    public ResponseEntity<PostCreateResponse> createPost(@PathVariable("types") PostCreateType postCreateType,
                                                         @RequestBody PostCreateRequest postCreateRequest,
                                                         @CurrentUser User authUser){
        Post post = postCreateFactory.create(postCreateType, postCreateRequest, authUser);
        PostCreateResponse postCreateResponse = PostCreateResponse.builder().postId(post.getId()).message("Create post successfully")
                .url("http://localhost:5173/test").build();
        return ResponseEntity.status(HttpStatus.CREATED).body(postCreateResponse);
    }

    // Role: User, Use-case Role: Post Creator
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId, @CurrentUser User authUser){
        postService.deletePost(postId, authUser);
        return ResponseEntity.ok("Delete post successfully");
    }

    // Role: User, Use-case Role: Post Creator
    @PutMapping("/{postId}")
    public ResponseEntity<PostCreateResponse> updateMyPost(@PathVariable Long postId,
                                                           @RequestBody PostUpdateRequest postUpdateRequest,
                                                           @CurrentUser User authUser){
        Post post = postService.updateMyPost(postId, postUpdateRequest, authUser);
        PostCreateResponse postCreateResponse = PostCreateResponse.builder().postId(post.getId()).message("Update post successfully")
                .url("http://localhost:5173/test").build();
        return ResponseEntity.ok(postCreateResponse);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostGetResponse> getDetailPost(@PathVariable Long postId){
        return ResponseEntity.ok(postQueryService.viewPostDetail(postId));
    }

    //=============================== For contributor API groups ==================================
    // Role: User, Use-case Role: Post Creator
    @GetMapping("/{postId}/attach")
    public ResponseEntity<Page<RequestAttachResponse>> getRequestAttachingList(@PathVariable Long postId,
                                                                               @CurrentUser User user,
                                                                               Pageable pageable){
        return ResponseEntity.ok(postContributeService.getRequestAttach(postId, user, pageable));
    }

    // Role: User
    @PostMapping("/{postId}/course/{courseId}/attach")
    public ResponseEntity<String> requestToAttachCourseToPost(@PathVariable Long postId,
                                                              @PathVariable Long courseId,
                                                              @CurrentUser User user){
        postContributeService.requestCourseToPost(postId, courseId, user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Send request to attach this course successfully");
    }

    // Role: User, Use-case Role: Post Creator
    @PostMapping("/{postId}/course/{courseId}/user/{targetManId}/attach/approved")
    public ResponseEntity<String> approveCourseRequest(@PathVariable Long postId,
                                                       @PathVariable Long courseId,
                                                       @PathVariable Long targetManId,
                                                       @CurrentUser User user){
        postContributeService.approveCourseToPost(postId, courseId, user, targetManId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Accept request to attach this course successfully");
    }

    // Role: User, Use-case Role: Post Creator
    @PostMapping("/{postId}/course/{courseId}/attach/declined")
    public ResponseEntity<String> declineCourseRequest(@PathVariable Long postId,
                                                       @PathVariable Long courseId,
                                                       @RequestBody String reason,
                                                       @CurrentUser User user){
        postContributeService.declineCourseToPost(postId, courseId, reason, user);
        return ResponseEntity.ok().body("Decline request to attach this course successfully");
    }

    // Role: User, Use-case Role: Post Creator
    @PostMapping("/{postId}/user/{targetManId}/banned")
    public ResponseEntity<String> banUserFromAttachingCourse(@PathVariable Long postId,
                                                       @PathVariable Long targetManId,
                                                       @CurrentUser User user){
        postContributeService.banThisUserToContribute(postId, targetManId, user);
        return ResponseEntity.ok().body("This user has been banned from contributing your post");
    }

    // Role: User, Use-case Role: Post Creator
    @DeleteMapping("/{postId}/user/{targetManId}/unbanned")
    public ResponseEntity<String> unbanUserFromAttachingCourse(@PathVariable Long postId,
                                                             @PathVariable Long targetManId,
                                                             @CurrentUser User user){
        postContributeService.removeBan(postId, targetManId, user);
        return ResponseEntity.ok().body("This user has been unbanned from contributing your post");
    }

    // Role: User, Use-case Role: Post Creator
    @DeleteMapping("/{postId}/course/{courseId}/remove")
    public ResponseEntity<String> removeCourseFromPost(@PathVariable Long postId,
                                                              @PathVariable Long courseId,
                                                              @CurrentUser User user){
        postContributeService.removeCourseFromPost(postId, courseId, user);
        return ResponseEntity.ok("Remove this course from post successfully");
    }
}
