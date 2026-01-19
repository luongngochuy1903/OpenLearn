package com.example.online.course.controller;

import com.example.online.annotation.CurrentUser;
import com.example.online.course.dto.*;
import com.example.online.course.service.CourseContributeService;
import com.example.online.domain.model.Course;
import com.example.online.course.service.CourseService;
import com.example.online.domain.model.RequestAttachModuleToCourse;
import com.example.online.domain.model.User;
import com.example.online.post.dto.RequestAttachResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final CourseContributeService courseContributeService;
    private static final Logger LOG = LoggerFactory.getLogger(CourseController.class);

    @PostMapping
    public ResponseEntity<CourseCreateResponse> createCourses(@Valid @RequestBody CourseCreateRequest courseCreateRequest,
                                                              @CurrentUser User user){
        LOG.info("POST /api/v1/courses - Body: CourseCreateRequest");
        Course course = courseService.createCourse(courseCreateRequest, user);
        CourseCreateResponse courseCreateResponse = CourseCreateResponse.builder().courseId(course.getId()).message("Create course successfully").build();
        return ResponseEntity.status(HttpStatus.CREATED).body(courseCreateResponse);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<String> deleteCourses(@PathVariable Long courseId, @CurrentUser User user){
        LOG.info("DELETE /api/v1/courses/{} - Body: Null", courseId);
        courseService.deleteCourse(courseId, user);
        return ResponseEntity.ok("Delete course successfully");
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<CourseCreateResponse> updateCourses(@PathVariable Long courseId, @RequestBody CourseUpdateRequest courseUpdateRequest,
                                                              @CurrentUser User user){
        Course course = courseService.updateCourse(courseId, courseUpdateRequest, user);
        CourseCreateResponse courseCreateResponse = CourseCreateResponse.builder().courseId(course.getId()).message("Update course successfully").build();
        return ResponseEntity.ok(courseCreateResponse);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseGetResponse> getDetailCourse(@PathVariable Long courseId, @CurrentUser User user){
        LOG.info("GET /api/v1/courses/{} - Body: Null", courseId);
        return ResponseEntity.ok(courseService.viewCourseDetail(courseId, user));
    }

    //====================== CONTRIBUTE TO COURSE API ==========================
    // Role: User, Use-case Role: Course Creator
    @GetMapping("/{courseId}/attach")
    public ResponseEntity<Page<CourseRequestAttachResponse>> getRequestAttachingList(@PathVariable Long courseId,
                                                                                     @CurrentUser User user,
                                                                                     Pageable pageable){
        return ResponseEntity.ok(courseContributeService.getRequestAttach(courseId, user, pageable));
    }

    // Role: User
    @PostMapping("/{courseId}/modules/{moduleId}/attach")
    public ResponseEntity<String> requestToAttachModuleToCourse(@PathVariable Long courseId,
                                                              @PathVariable Long moduleId,
                                                              @CurrentUser User user){
        courseContributeService.requestModuleToCourse(courseId, moduleId, user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Send request to attach this module successfully");
    }

    // Role: User, Use-case Role: Course Creator
    @PostMapping("/{courseId}/modules/{moduleId}/user/{targetManId}/attach/approved")
    public ResponseEntity<String> approveModuleRequest(@PathVariable Long courseId,
                                                       @PathVariable Long moduleId,
                                                       @PathVariable Long targetManId,
                                                       @CurrentUser User user){
        courseContributeService.approveModuleToCourse(courseId, moduleId, user, targetManId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Accept request to attach this module successfully");
    }

    // Role: User, Use-case Role: Course Creator
    @PostMapping("/{courseId}/modules/{moduleId}/attach/declined")
    public ResponseEntity<String> declineModuleRequest(@PathVariable Long courseId,
                                                       @PathVariable Long moduleId,
                                                       @RequestBody String reason,
                                                       @CurrentUser User user){
        courseContributeService.declineModuleToCourse(courseId, moduleId, reason, user);
        return ResponseEntity.ok().body("Decline request to attach this module successfully");
    }

    // Role: User, Use-case Role: Course Creator
    @PostMapping("/{courseId}/user/{targetManId}/banned")
    public ResponseEntity<String> banUserFromAttachingCourse(@PathVariable Long courseId,
                                                             @PathVariable Long targetManId,
                                                             @CurrentUser User user){
        courseContributeService.banThisUserToContribute(courseId, targetManId, user);
        return ResponseEntity.ok().body("This user has been banned from contributing your course");
    }

    // Role: User, Use-case Role: Course Creator
    @DeleteMapping("/{courseId}/user/{targetManId}/unbanned")
    public ResponseEntity<String> unbanUserFromAttachingCourse(@PathVariable Long courseId,
                                                               @PathVariable Long targetManId,
                                                               @CurrentUser User user){
        courseContributeService.removeBan(courseId, targetManId, user);
        return ResponseEntity.ok().body("This user has been unbanned from contributing your course");
    }

    // Role: User, Use-case Role: Course Creator
    @DeleteMapping("/{courseId}/modules/{moduleId}/remove")
    public ResponseEntity<String> removeCourseFromPost(@PathVariable Long courseId,
                                                       @PathVariable Long moduleId,
                                                       @CurrentUser User user){
        courseContributeService.removeModuleFromCourse(courseId, moduleId, user);
        return ResponseEntity.ok("Remove this course from post successfully");
    }
}
