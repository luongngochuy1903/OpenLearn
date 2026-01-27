package com.example.online.post.service.impl;

import com.example.online.annotation.CheckCommunityPostForMember;
import com.example.online.annotation.CheckPostCreator;
import com.example.online.domain.model.*;
import com.example.online.enumerate.BanType;
import com.example.online.enumerate.ContributorRole;
import com.example.online.enumerate.RequestStatus;
import com.example.online.event.PostChangedEvent;
import com.example.online.event.PostDeletedEvent;
import com.example.online.exception.AccessDeniedException;
import com.example.online.exception.BadRequestException;
import com.example.online.exception.ForbiddenException;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.post.dto.RequestAttachResponse;
import com.example.online.post.service.PostContributeService;
import com.example.online.postcourse.service.PostCourseService;
import com.example.online.repository.CourseRepository;
import com.example.online.repository.PostRepository;
import com.example.online.repository.RequestAttachCourseToPostRepository;
import com.example.online.utils.BanUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostContributeServiceImpl implements PostContributeService {
    private final CourseRepository courseRepository;
    private final PostRepository postRepository;
    private final BanUtils banUtils;
    private final PostCourseService postCourseService;
    private final RequestAttachCourseToPostRepository requestAttachCourseToPostRepository;
    private final ApplicationEventPublisher publisher;
    private static final Logger LOG = LoggerFactory.getLogger(PostContributeServiceImpl.class);

    /*
    Function: Get pending course attachment request of specific post.
    Business context role: Post Creator
     */
    @CheckPostCreator(postIdParam = "postId", userParam = "user")
    public Page<RequestAttachResponse> getRequestAttach(Long postId, User user, Pageable pageable){
        Page<RequestAttachCourseToPost> requests = requestAttachCourseToPostRepository.findAllByPost_IdAndStatusAndCourseIsNotNull(postId, RequestStatus.PENDING, pageable);
        return requests.map(req -> RequestAttachResponse.builder()
                .courseId(req.getCourse().getId())
                .courseName(req.getCourse().getName())
                .courseURL("URL to course")
                .creatorId(req.getUser().getId())
                .creatorName(req.getUser().getFirstName() + " " + req.getUser().getLastName())
                .avatarURL("Avatar_url")
                .build()
        );
    }

    /*
    Function: Post creator accept course attached request from users
    Business Context Role: Post Creator
     */
    //Check if user is post admin (@CheckPostCreator)
    @CheckPostCreator(postIdParam = "postId", userParam = "user")
    public void approveCourseToPost(Long postId, Long courseId, User user, Long courseCreatorId){
        if (courseId == null){
            throw new BadRequestException("courseId could not be null");
        }

        if (banUtils.checkUserBan(courseCreatorId, BanType.ATTACH_COURSE_TO_POST, postId)) {
            throw new BadRequestException(String.format("User %s has been banned from this action", user.getFirstName() + " " + user.getLastName()));
        }

        RequestAttachCourseToPost req = requestAttachCourseToPostRepository
                .findByPost_IdAndCourse_IdAndStatus(postId, courseId, RequestStatus.PENDING)
                .orElseThrow(() -> new ResourceNotFoundException("Something gone wrong! Request not found"));
        requestAttachCourseToPostRepository.delete(req);
        postCourseService.createPostCourse(req.getPost(), req.getCourse(), req.getUser(), ContributorRole.CONTRIBUTOR);
        publisher.publishEvent(new PostChangedEvent(postId));
        LOG.info("{} approved course with id {} to his/her post {}", user.getFirstName() + " " + user.getLastName(),
                courseId, postId);
    }

    /*
    Function: Post creator decline course attached request from users
    Business Context Role: Post Creator
     */
    @CheckPostCreator(postIdParam = "postId", userParam = "user")
    public void declineCourseToPost(Long postId, Long courseId, String reason, User user){
        if (courseId == null){
            throw new BadRequestException("courseId could not be null");
        }
        RequestAttachCourseToPost req = requestAttachCourseToPostRepository
                .findByPost_IdAndCourse_IdAndStatus(postId, courseId, RequestStatus.PENDING)
                .orElseThrow(() -> new ResourceNotFoundException("Something gone wrong! Request not found"));
//        if (req.getStatus().equals(RequestStatus.REJECT)){
//            throw new BadRequestException("This request has already been rejected");
//        }
        req.setReasonReject(reason);
        req.setReviewedAt(LocalDateTime.now());
        req.setReviewedBy(user);
        req.setStatus(RequestStatus.REJECT);
        requestAttachCourseToPostRepository.save(req);
        LOG.info("{} declined course with id {} to his/her post {}", user.getFirstName() + " " + user.getLastName(),
                courseId, postId);
    }

    /*
    Function: Ban users from contributing their course to own post.
    Business context role: Post Creator
     */
    @CheckPostCreator(postIdParam = "postId", userParam = "user")
    public void banThisUserToContribute(Long postId, Long targetManId, User user){
        if (targetManId == null){
            throw new BadRequestException("courseId could not be null");
        }

        banUtils.addBanRecord(targetManId, BanType.ATTACH_COURSE_TO_POST, postId);

        LOG.info("{} banned user with id {} from his/her post {}", user.getFirstName() + " " + user.getLastName(),
                targetManId, postId);
    }
    @CheckPostCreator(postIdParam = "postId", userParam = "user")
    public void removeBan(Long postId, Long targetManId, User user){
        if (targetManId == null){
            throw new BadRequestException("targetManId could not be null");
        }

        banUtils.removeBanRecord(targetManId, BanType.ATTACH_COURSE_TO_POST , postId);

        LOG.info("{} unbanned user with id {} from his/her community {}", user.getFirstName() + " " + user.getLastName(),
                targetManId, postId);
    }

    /*
    Function: Remove own course from post attaching before
    Business Context Role: Post Contributor, Post Creator
     */
    @CheckCommunityPostForMember(postIdParam = "postId", userParam = "user")
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
    @CheckCommunityPostForMember(postIdParam = "postId", userParam = "user")
    @Transactional
    public void requestCourseToPost(Long postId, Long courseId, User user) {

        if (postId == null || courseId == null) {
            throw new BadRequestException("postId and courseId must not be null");
        }

        banUtils.checkBan(user.getId(), BanType.ATTACH_COURSE_TO_POST, postId);

        // Course belongs to user
        Course course = courseRepository.findByIdAndCreator_Id(courseId, user.getId())
                .orElseThrow(() -> new ForbiddenException("You are not the creator of this course"));

        // Post exists
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found"));


        // Check if this request is duplicated
        RequestAttachCourseToPost existingReq = requestAttachCourseToPostRepository.findByPost_IdAndCourse_IdAndStatus(postId, courseId, RequestStatus.PENDING)
                .orElse(null);

        if (existingReq != null) {
            RequestStatus status = existingReq.getStatus();

            if (status == RequestStatus.PENDING) {
                throw new BadRequestException("Request is already pending");
            }

        }

        // If course is already attached
        if (postCourseService.checkExistsByPostAndCourse(postId, courseId)) {
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
