package com.example.online.module.service.impl;

import com.example.online.annotation.CheckModuleCreator;
import com.example.online.coursemodule.service.CourseModuleService;
import com.example.online.domain.model.CourseModule;
import com.example.online.domain.model.Lesson;
import com.example.online.domain.model.User;
import com.example.online.event.ModuleChangedEvent;
import com.example.online.exception.ForbiddenException;
import com.example.online.exception.UnauthorizedException;
import com.example.online.lesson.service.LessonService;
import com.example.online.module.dto.ModuleCreateRequest;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.domain.model.Module;
import com.example.online.module.dto.ModuleUpdateRequest;
import com.example.online.repository.ModuleRepository;
import com.example.online.module.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ApplicationEventPublisher publisher;

    public Module createModule(ModuleCreateRequest moduleCreateRequest, User user){
        if (user == null) {
            throw new UnauthorizedException("You need to login first");
        }
        Set<Lesson> LessonSet = moduleCreateRequest.getLessonCreateRequests().stream().map(lessonService::createLesson).collect(Collectors.toSet());

        Module module = Module.builder().name(moduleCreateRequest.getName()).description(moduleCreateRequest.getDescription()).courseModules(new HashSet<>())
                .creator(user)
                .lessons(LessonSet).createdAt(LocalDateTime.now()).updateAt(LocalDateTime.now()).build();
        return saveModule(module);
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
            module.setLessons(lessonSet);
        }
        boolean existsInAnyCourse = courseModuleService.moduleExistsInAnyCourse(moduleId);
        if (existsInAnyCourse){
            publisher.publishEvent(new ModuleChangedEvent(moduleId));
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
        moduleRepository.delete(module);
    }
}
