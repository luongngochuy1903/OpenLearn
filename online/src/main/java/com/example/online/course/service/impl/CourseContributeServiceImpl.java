package com.example.online.course.service.impl;

import com.example.online.annotation.CheckCourseCreator;
import com.example.online.course.dto.CourseRequestAttachResponse;
import com.example.online.course.service.CourseContributeService;
import com.example.online.coursemodule.service.CourseModuleService;
import com.example.online.domain.model.*;
import com.example.online.domain.model.Module;
import com.example.online.enumerate.BanType;
import com.example.online.enumerate.ContributorRole;
import com.example.online.enumerate.RequestStatus;
import com.example.online.event.CourseChangedEvent;
import com.example.online.event.PostChangedEvent;
import com.example.online.exception.AccessDeniedException;
import com.example.online.exception.BadRequestException;
import com.example.online.exception.ForbiddenException;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.repository.CourseRepository;
import com.example.online.repository.ModuleRepository;
import com.example.online.repository.RequestAttachModuleToCourseRepository;
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
public class CourseContributeServiceImpl implements CourseContributeService {
    private final RequestAttachModuleToCourseRepository requestAttachModuleToCourseRepository;
    private final BanUtils banUtils;
    private final CourseModuleService courseModuleService;
    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final ApplicationEventPublisher publisher;
    private static final Logger LOG = LoggerFactory.getLogger(CourseContributeServiceImpl.class);

    /*
    Function: Get pending module attachment request of specific course.
    Business context role: Course Creator
     */
    @CheckCourseCreator(courseIdParam = "courseId", userParam = "user")
    public Page<CourseRequestAttachResponse> getRequestAttach(Long courseId, User user, Pageable pageable){
        Page<RequestAttachModuleToCourse> requests = requestAttachModuleToCourseRepository.findAllByCourse_IdAndStatusAndModuleIsNotNull(courseId, RequestStatus.PENDING, pageable);
        return requests.map(req -> CourseRequestAttachResponse.builder()
                .moduleId(req.getModule().getId())
                .moduleName(req.getModule().getName())
                .moduleURL("URL to module")
                .creatorId(req.getUser().getId())
                .creatorName(req.getUser().getFirstName() + " " + req.getUser().getLastName())
                .avatarURL("Avatar_url")
                .build()
        );
    }

    /*
    Function: Course creator accept module attached request from users
    Business Context Role: Course Creator
     */
    //Check if user is post admin (@CheckPostCreator)
    @CheckCourseCreator(courseIdParam = "courseId", userParam = "user")
    public void approveModuleToCourse(Long courseId, Long moduleId, User user, Long moduleCreatorId){
        if (moduleId == null){
            throw new BadRequestException("moduleId could not be null");
        }

        if (moduleCreatorId == null){
            throw new BadRequestException("moduleCreatorId could not be null");
        }

        if (banUtils.checkUserBan(moduleCreatorId, BanType.ATTACH_MODULE_TO_COURSE, courseId)) {
            throw new BadRequestException(String.format("User %s has been banned from this action", user.getFirstName() + " " + user.getLastName()));
        }

        RequestAttachModuleToCourse req = requestAttachModuleToCourseRepository
                .findByCourse_IdAndModule_IdAndStatus(courseId, moduleId, RequestStatus.PENDING)
                .orElseThrow(() -> new ResourceNotFoundException("Something gone wrong! Request not found"));
        requestAttachModuleToCourseRepository.delete(req);
        courseModuleService.createCourseModule(req.getUser(), req.getModule(), req.getCourse(), ContributorRole.CONTRIBUTOR);
        publisher.publishEvent(new CourseChangedEvent(courseId));
        LOG.info("{} approved module with id {} to his/her course {}", user.getFirstName() + " " + user.getLastName(),
                moduleId, courseId);
    }

    /*
    Function: Post creator decline course attached request from users
    Business Context Role: Post Creator
     */
    @CheckCourseCreator(courseIdParam = "courseId", userParam = "user")
    public void declineModuleToCourse(Long courseId, Long moduleId, String reason, User user){
        if (moduleId == null){
            throw new BadRequestException("moduleId could not be null");
        }
        RequestAttachModuleToCourse req = requestAttachModuleToCourseRepository
                .findByCourse_IdAndModule_IdAndStatus(courseId, moduleId, RequestStatus.PENDING)
                .orElseThrow(() -> new ResourceNotFoundException("Something gone wrong! Request not found"));
//        if (req.getStatus().equals(RequestStatus.REJECT)){
//            throw new BadRequestException("This request has already been rejected");
//        }
        req.setReasonReject(reason);
        req.setReviewedAt(LocalDateTime.now());
        req.setReviewedBy(user);
        req.setStatus(RequestStatus.REJECT);
        requestAttachModuleToCourseRepository.save(req);
        LOG.info("{} declined module with id {} to his/her course {}", user.getFirstName() + " " + user.getLastName(),
                moduleId, courseId);
    }

    /*
    Function: Ban users from contributing their module to own course.
    Business context role: Course Creator
     */
    @CheckCourseCreator(courseIdParam = "courseId", userParam = "user")
    public void banThisUserToContribute(Long courseId, Long targetManId, User user){
        if (targetManId == null){
            throw new BadRequestException("targetManId could not be null");
        }

        banUtils.addBanRecord(targetManId, BanType.ATTACH_MODULE_TO_COURSE, courseId);

        LOG.info("{} banned user with id {} from his/her course {}", user.getFirstName() + " " + user.getLastName(),
                targetManId, courseId);
    }

    /*
    Function: Unban users from contributing their module to own course.
    Business context role: Course Creator
     */
    @CheckCourseCreator(courseIdParam = "courseId", userParam = "user")
    public void removeBan(Long courseId, Long targetManId, User user){
        if (targetManId == null){
            throw new BadRequestException("targetManId could not be null");
        }

        banUtils.removeBanRecord(targetManId, BanType.ATTACH_MODULE_TO_COURSE , courseId);

        LOG.info("{} unbanned user with id {} from his/her course {}", user.getFirstName() + " " + user.getLastName(),
                targetManId, courseId);
    }

    /*
    Function: Remove own course from post attaching before
    Business Context Role: Course Creator
     */
    public void removeModuleFromCourse(Long courseId, Long moduleId, User user){
        if (courseId == null){
            throw new BadRequestException("courseId could not be null");
        }
        if (moduleId == null){
            throw new BadRequestException("moduleId could not be null");
        }
        //Chỗ này check ki
        CourseModule courseModule = courseModuleService.findCourseModuleByCourseIdAndModuleId(courseId, moduleId);
        boolean isCourseCreator = courseModule.getCourse().getCreator().getId().equals(user.getId());
        boolean isModuleCreator = courseModule.getModule().getCreator().getId().equals(user.getId());

        if (isModuleCreator || isCourseCreator){
            courseModuleService.deleteCourseModule(courseModule);
        }
        else {
            throw new AccessDeniedException("You don't have permission to perform this action");
        }
        LOG.info("User {} removed module {} from course {}", user.getEmail(), moduleId, courseId);
    }


    /*
    Function: Send request to attach my module to course
    Business Context Role: User
     */
    @Transactional
    public void requestModuleToCourse(Long courseId, Long moduleId, User user) {

        if (moduleId == null || courseId == null) {
            throw new BadRequestException("moduleId and courseId must not be null");
        }

        banUtils.checkBan(user.getId(), BanType.ATTACH_MODULE_TO_COURSE, courseId);

        // Course belongs to user
        Module module = moduleRepository.findByIdAndCreator_Id(moduleId, user.getId())
                .orElseThrow(() -> new ForbiddenException("You are not the creator of this module"));

        // Post exists
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found"));


        // Check if this request is duplicated
        RequestAttachModuleToCourse existingReq = requestAttachModuleToCourseRepository.findByCourse_IdAndModule_IdAndStatus(courseId, moduleId, RequestStatus.PENDING)
                .orElse(null);

        if (existingReq != null) {
            RequestStatus status = existingReq.getStatus();

            if (status == RequestStatus.PENDING) {
                throw new BadRequestException("Request is already pending");
            }

        }

        // If course is already attached
        if (courseModuleService.checkExistsByCourseAndModule(courseId, moduleId)) {
            throw new BadRequestException("This module is already attached to the course");
        }

        RequestAttachModuleToCourse request = RequestAttachModuleToCourse.builder()
                .course(course)
                .module(module)
                .user(user)
                .status(RequestStatus.PENDING)
                .sendAt(LocalDateTime.now())
                .build();

        requestAttachModuleToCourseRepository.save(request);
        LOG.info("User {} requested to attach module {} to course {}", user.getEmail(), moduleId, courseId);
    }
}
