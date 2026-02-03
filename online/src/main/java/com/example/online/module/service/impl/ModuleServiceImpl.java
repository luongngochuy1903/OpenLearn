package com.example.online.module.service.impl;

import com.example.online.annotation.CheckModuleCreator;
import com.example.online.coursemodule.service.CourseModuleService;
import com.example.online.domain.model.*;
import com.example.online.domain.model.Module;
import com.example.online.enumerate.ESType;
import com.example.online.enumerate.OutboxEventType;
import com.example.online.enumerate.OutboxStatus;
import com.example.online.exception.BadRequestException;
import com.example.online.exception.ForbiddenException;
import com.example.online.exception.UnauthorizedException;
import com.example.online.lesson.service.LessonService;
import com.example.online.module.dto.ModuleCreateRequest;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.module.dto.ModuleUpdateRequest;
import com.example.online.repository.ModuleRepository;
import com.example.online.module.service.ModuleService;
import com.example.online.repository.OutboxRepository;
import com.example.online.worker.OutboxWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {
    private final ModuleRepository moduleRepository;
    private final LessonService lessonService;
    private final CourseModuleService courseModuleService;
    private final OutboxWorker outboxWorker;
    private final OutboxRepository outboxRepository;

    public Module createModule(ModuleCreateRequest moduleCreateRequest, User user){
        if (user == null) {
            throw new UnauthorizedException("You need to login first");
        }

        Module module = Module.builder().name(moduleCreateRequest.getName())
                .description(moduleCreateRequest.getDescription())
                .courseModules(new HashSet<>())
                .creator(user)
                .build();
        Module savedModule = saveModule(module);
        Set<Lesson> lessonSet = moduleCreateRequest.getLessonCreateRequests().stream()
                .map(lessonCreateRequest -> lessonService.createLesson(lessonCreateRequest, module))
                .collect(Collectors.toSet());
        savedModule.setLessons(lessonSet);
        return savedModule;
    }

    public Module saveModule(Module module){
        return moduleRepository.save(module);
    }

    @Transactional
    @CheckModuleCreator(moduleIdParam = "moduleId", userParam = "user")
    public Module updateModule(Long moduleId, ModuleUpdateRequest moduleUpdateRequest, User user){
        if (user == null) {
            throw new UnauthorizedException("You need to login first");
        }
        Module module = moduleRepository.findById(moduleId).orElseThrow(() -> new ResourceNotFoundException("Module not found !"));
        User creator = module.getCreator();

        if (!creator.getId().equals(user.getId())){
            throw new ForbiddenException("You don't have permission to modify this module");
        }
        if (moduleUpdateRequest.getName() != null){
            module.setName(moduleUpdateRequest.getName());
        }
        if (moduleUpdateRequest.getDescription() != null){
            module.setDescription(moduleUpdateRequest.getDescription());
        }
        if (moduleUpdateRequest.getLessonUpdateRequests() != null && !moduleUpdateRequest.getLessonUpdateRequests().isEmpty()){
            Set<Lesson> lessonSet = moduleUpdateRequest.getLessonUpdateRequests().stream().map(lessonService::updateLesson).collect(Collectors.toSet());
        }
        boolean existsInAnyCourse = courseModuleService.moduleExistsInAnyCourse(moduleId);
        if (existsInAnyCourse){
            List<Long> courseIds = courseModuleService.getCoursesIdByModule(moduleId);
            for (var courseId : courseIds) {
                OutBoxEvent outBoxEvent = outboxWorker.getOutBoxEvent(courseId, ESType.COURSE, List.of(OutboxStatus.NEW, OutboxStatus.FAILED, OutboxStatus.PROCESSING));
                if (outBoxEvent != null) {
                    if (outBoxEvent.getStatus().equals(OutboxStatus.PROCESSING)) {
                        throw new BadRequestException("Something happened! Please try again later");
                    }
                    outBoxEvent.setStatus(OutboxStatus.NEW);
                    outBoxEvent.setEventType(OutboxEventType.CHANGED);
                }
                else{
                    outBoxEvent = OutBoxEvent.builder()
                            .aggregateId(courseId)
                            .eventType(OutboxEventType.CHANGED)
                            .type(ESType.COURSE)
                            .status(OutboxStatus.NEW)
                            .createdAt(Instant.now())
                            .build();
                }
                outboxRepository.save(outBoxEvent);
            }
        }
        return saveModule(module);
    }

    @Transactional
    @CheckModuleCreator(moduleIdParam = "moduleId", userParam = "user")
    public void deleteModule(Long moduleId, User user){
        if (user == null) {
            throw new UnauthorizedException("You need to login first");
        }
        Module module = moduleRepository.findById(moduleId).orElseThrow(() -> new ResourceNotFoundException("Module not found !"));
        User creator = module.getCreator();

        if (!creator.getId().equals(user.getId())){
            throw new ForbiddenException("You don't have permission to modify this module");
        }
        List<Long> courseIds = courseModuleService.getCoursesIdByModule(moduleId);
        boolean existsInAnyCourse = courseModuleService.moduleExistsInAnyCourse(moduleId);
        moduleRepository.delete(module);
        if (existsInAnyCourse){
            for (var courseId : courseIds) {
                OutBoxEvent outBoxEvent = outboxWorker.getOutBoxEvent(courseId, ESType.COURSE, List.of(OutboxStatus.NEW, OutboxStatus.FAILED, OutboxStatus.PROCESSING));
                if (outBoxEvent != null) {
                    if (outBoxEvent.getStatus().equals(OutboxStatus.PROCESSING)) {
                        throw new BadRequestException("Something happened! Please try again later");
                    }
                    outBoxEvent.setStatus(OutboxStatus.NEW);
                    outBoxEvent.setEventType(OutboxEventType.CHANGED);
                }
                else{
                    outBoxEvent = OutBoxEvent.builder()
                            .aggregateId(courseId)
                            .eventType(OutboxEventType.CHANGED)
                            .type(ESType.COURSE)
                            .status(OutboxStatus.NEW)
                            .createdAt(Instant.now())
                            .build();
                }
                outboxRepository.save(outBoxEvent);
            }
        }
    }
}
