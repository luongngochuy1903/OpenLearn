package com.example.online.annotation.aspect;

import com.example.online.annotation.CheckCommunityMember;
import com.example.online.annotation.CheckCommunityPostForMember;
import com.example.online.community.service.CommunityMemberService;
import com.example.online.domain.model.Post;
import com.example.online.domain.model.User;
import com.example.online.exception.AccessDeniedException;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class CommunityMemberCheckAspect {
    private final CommunityMemberService communityMemberService;

    @Around("@annotation(checkCommunityMember)")
    public Object checkCreator(
            ProceedingJoinPoint joinPoint,
            CheckCommunityMember checkCommunityMember
    ) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        Long communityId = null;
        Object userObj = null;

        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals(checkCommunityMember.communityIdParam())) {
                communityId = (Long) args[i];
            }
            if (paramNames[i].equals(checkCommunityMember.userParam())) {
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

        if (!communityMemberService.isMember(user.getId(), communityId)) {
            throw new AccessDeniedException(String
                    .format("User %s is not in this community", user.getFirstName() + " " + user.getLastName()));
        }

        return joinPoint.proceed();
    }
}
