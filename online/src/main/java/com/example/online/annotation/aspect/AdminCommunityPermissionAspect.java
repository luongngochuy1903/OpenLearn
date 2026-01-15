package com.example.online.annotation.aspect;

import com.example.online.annotation.CheckCommunityAdmin;
import com.example.online.community.service.CommunityMemberService;
import com.example.online.domain.model.User;
import com.example.online.exception.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;

@Service
@Aspect
@RequiredArgsConstructor
public class AdminCommunityPermissionAspect {
    private final CommunityMemberService communityMemberService;

    @Around("@annotation(checkCommunityAdmin)")
    public Object checkCreator(
            ProceedingJoinPoint joinPoint,
            CheckCommunityAdmin checkCommunityAdmin
    ) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        Long communityId = null;
        Object userObj = null;

        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals(checkCommunityAdmin.communityIdParam())) {
                communityId = (Long) args[i];
            }
            if (paramNames[i].equals(checkCommunityAdmin.userParam())) {
                userObj = args[i];
            }
        }

        if (communityId == null) {
            throw new IllegalStateException("communityId parameter not found");
        }
        if (userObj == null) {
            throw new IllegalStateException("User parameter not found");
        }

        if (!(userObj instanceof User user)) {
            throw new IllegalStateException("User parameter must be of type User");
        }

        if (!communityMemberService.isAdmin(user.getId(), communityId)){
            throw new AccessDeniedException("You don't have permission to access this resource");
        }

        return joinPoint.proceed();
    }
}
