package com.example.online.annotation.aspect;

import com.example.online.annotation.CheckModuleCreator;
import com.example.online.domain.model.Module;
import com.example.online.domain.model.User;
import com.example.online.exception.ForbiddenException;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Aspect
@RequiredArgsConstructor
public class ModulePermissionAspect {
    private final ModuleRepository moduleRepository;

    @Around("@annotation(CheckModuleCreator)")
    public Object checkCreator(
            ProceedingJoinPoint joinPoint,
            CheckModuleCreator checkModuleCreator
    ) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        Long moduleId = null;
        Object userObj = null;

        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals(checkModuleCreator.moduleIdParam())) {
                moduleId = (Long) args[i];
            }
            if (paramNames[i].equals(checkModuleCreator.userParam())) {
                userObj = args[i];
            }
        }

        if (moduleId == null) {
            throw new IllegalStateException("moduleId parameter not found");
        }
        if (userObj == null) {
            throw new IllegalStateException("User parameter not found");
        }

        if (!(userObj instanceof User user)) {
            throw new IllegalStateException("User parameter must be of type User");
        }

        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("module not found"));

        if (!module.getCreator().getId().equals(user.getId())) {
            throw new ForbiddenException("You don't have permission to modify this module");
        }

        return joinPoint.proceed();
    }
}
