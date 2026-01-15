package com.example.online.annotation.aspect;

import com.example.online.annotation.CheckPostCreator;
import com.example.online.domain.model.Post;
import com.example.online.domain.model.User;
import com.example.online.exception.ForbiddenException;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
@RequiredArgsConstructor
public class PostPermissionAspect {
    private final PostRepository postRepository;
    private static final Logger LOG = LoggerFactory.getLogger(PostPermissionAspect.class);

    @Around("@annotation(checkPostCreator)")
    public Object checkCreator(
            ProceedingJoinPoint joinPoint,
            CheckPostCreator checkPostCreator
    ) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        LOG.info("Post Param names: {}", Arrays.toString(paramNames));
        Object[] args = joinPoint.getArgs();

        Long postId = null;
        Object userObj = null;

        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals(checkPostCreator.postIdParam())) {
                postId = (Long) args[i];
            }
            if (paramNames[i].equals(checkPostCreator.userParam())) {
                userObj = args[i];
            }
        }

        if (postId == null) {
            throw new IllegalStateException("postId parameter not found");
        }
        if (userObj == null) {
            throw new IllegalStateException("User parameter not found");
        }

        if (!(userObj instanceof User user)) {
            throw new IllegalStateException("User parameter must be of type User");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("post not found"));

        if (!post.getCreator().getId().equals(user.getId())) {
            throw new ForbiddenException("You don't have permission to modify this post");
        }

        return joinPoint.proceed();
    }
}
