package com.example.online.post.service.impl;

import com.example.online.annotation.CheckCommunityPostRole;
import com.example.online.annotation.CheckPostCreator;
import com.example.online.domain.model.*;
import com.example.online.enumerate.BanType;
import com.example.online.enumerate.RequestStatus;
import com.example.online.exception.AccessDeniedException;
import com.example.online.exception.BadRequestException;
import com.example.online.exception.ForbiddenException;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.post.service.PostContributeService;
import com.example.online.postcourse.service.PostCourseService;
import com.example.online.repository.CourseRepository;
import com.example.online.repository.PostRepository;
import com.example.online.repository.RequestAttachCourseToPostRepository;
import com.example.online.utils.BanUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostContributeServiceImpl implements PostContributeService {
    private final CourseRepository courseRepository;
    private final PostRepository postRepository;
    private final BanUtils banUtils;
    private final PostCourseService postCourseService;
    private final RequestAttachCourseToPostRepository requestAttachCourseToPostRepository;
    private static final Logger LOG = LoggerFactory.getLogger(PostContributeServiceImpl.class);

    /*
    Function: Post creator accept course attached request from users
    Business Context Role: Post Creator
     */
    //Check if user is post admin (@CheckPostCreator)
    @CheckPostCreator(postIdParam = "postId", userParam = "user")
    public void approveCourseToPost(Long postId, Long courseId, User user){
        if (courseId == null){
            throw new BadRequestException("courseId could not be null");
        }

        RequestAttachCourseToPost req = requestAttachCourseToPostRepository
                .findByPost_IdAndCourse_IdAndStatus(postId, courseId, RequestStatus.PENDING)
                .orElseThrow(() -> new ResourceNotFoundException("Something gone wrong! Request not found"));
        requestAttachCourseToPostRepository.delete(req);
        LOG.info("{} approved course with id {} to his/her post {}", user.getFirstName() + " " + user.getLastName(),
                courseId, postId);
    }

    @CheckPostCreator(postIdParam = "postId", userParam = "user")
    public void declineCourseToPost(Long postId, Long courseId, String reason, User user){
        if (courseId == null){
            throw new BadRequestException("courseId could not be null");
        }
        RequestAttachCourseToPost req = requestAttachCourseToPostRepository
                .findByPost_IdAndCourse_Id(postId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Something gone wrong! Request not found"));

        req.setReasonReject(reason);
        req.setReviewedAt(LocalDateTime.now());
        req.setReviewedBy(user);
        req.setStatus(RequestStatus.REJECT);
        requestAttachCourseToPostRepository.save(req);
        LOG.info("{} declined course with id {} to his/her post {}", user.getFirstName() + " " + user.getLastName(),
                courseId, postId);
    }

    @CheckPostCreator(postIdParam = "postId", userParam = "user")
    public void banThisUserToContribute(Long postId, Long courseId, User user){
        if (courseId == null){
            throw new BadRequestException("courseId could not be null");
        }
        RequestAttachCourseToPost req = requestAttachCourseToPostRepository
                .findByPost_IdAndCourse_Id(postId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Something gone wrong! Request not found"));

        req.setReasonReject(reason);
        req.setReviewedAt(LocalDateTime.now());
        req.setReviewedBy(user);
        req.setStatus(RequestStatus.REJECT);
        requestAttachCourseToPostRepository.save(req);
        LOG.info("{} declined course with id {} to his/her post {}", user.getFirstName() + " " + user.getLastName(),
                courseId, postId);
    }

    /*
    Function: Remove own course from post attaching before
    Business Context Role: Post Contributor, Post Creator
     */
    @CheckCommunityPostRole(postIdParam = "postId", userParam = "user")
    public void removeCourseFromPost(Long postId, Long courseId, User user){
        if (courseId == null){
            throw new BadRequestException("courseId could not be null");
        }
        if (postId == null){
            throw new BadRequestException("postId could not be null");
        }
        //Chỗ này check ki
        PostCourse postCourse = postCourseService.findPostCourseByPostIdAndCourseId(postId, courseId);
        boolean isCourseCreator = postCourse.getCourse().getCreator().getId().equals(user.getId());
        boolean isPostCreator = postCourse.getPost().getCreator().getId().equals(user.getId());

        if (isPostCreator || isCourseCreator){
            postCourseService.deletePostCourse(postCourse);
        }
        else {
            throw new AccessDeniedException("You don't have permission to perform this action");
        }
        LOG.info("User {} removed course {} from post {}", user.getEmail(), courseId, postId);
    }


    /*
    Function: Send request to attach my course to post
    Business Context Role: User
     */
    @CheckCommunityPostRole(postIdParam = "postId", userParam = "user")
    @Transactional
    public void requestCourseToPost(Long postId, Long courseId, User user) {

        if (postId == null || courseId == null) {
            throw new BadRequestException("postId and courseId must not be null");
        }

        // Course belongs to user
        Course course = courseRepository.findByIdAndCreator_Id(courseId, user.getId())
                .orElseThrow(() -> new ForbiddenException("You are not the creator of this course"));

        // Post exists
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        banUtils.checkBan(user, BanType.ATTACH_COURSE_TO_POST, postId);

        // Check if this request is duplicated
        RequestAttachCourseToPost existingReq = requestAttachCourseToPostRepository.findByPost_IdAndCourse_Id(postId, courseId).orElse(null);

        if (existingReq != null) {
            RequestStatus status = existingReq.getStatus();

            if (status == RequestStatus.PENDING) {
                throw new BadRequestException("Request is already pending");
            }

        }

        // If course is already attached
        if (postCourseService.checkExistsByPostAndPost(postId, courseId)) {
            throw new BadRequestException("This course is already attached to the post");
        }

        RequestAttachCourseToPost request = RequestAttachCourseToPost.builder()
                .post(post)
                .course(course)
                .user(user)
                .status(RequestStatus.PENDING)
                .sendAt(LocalDateTime.now())
                .build();

        requestAttachCourseToPostRepository.save(request);
        LOG.info("User {} requested to attach course {} to post {}", user.getEmail(), courseId, postId);
    }
}
