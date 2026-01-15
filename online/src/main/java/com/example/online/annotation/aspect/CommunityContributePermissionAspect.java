package com.example.online.annotation.aspect;

import com.example.online.annotation.CheckCommunityPostRole;
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
public class CommunityContributePermissionAspect {
    private final CommunityMemberService communityMemberService;
    private final PostRepository postRepository;

    @Around("@annotation(checkCommunityPostRole)")
    public Object checkCreator(
            ProceedingJoinPoint joinPoint,
            CheckCommunityPostRole checkCommunityRole
    ) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        Long postId = null;
        Object userObj = null;

        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals(checkCommunityRole.postIdParam())) {
                postId = (Long) args[i];
            }
            if (paramNames[i].equals(checkCommunityRole.userParam())) {
                userObj = args[i];
            }
        }

        if (postId == null) {
            throw new IllegalStateException("communityId parameter not found");
        }
        if (userObj == null) {
            throw new IllegalStateException("User parameter not found");
        }

        if (!(userObj instanceof User user)) {
            throw new IllegalStateException("User parameter must be of type User");
        }

        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        if (post.getCommunity() != null) {
            if (!communityMemberService.isMember(user.getId(), post.getCommunity().getId())) {
                throw new AccessDeniedException(String
                        .format("User %s is not in this community", user.getFirstName() + " " + user.getLastName()));
            }
        }

        return joinPoint.proceed();
    }
}
