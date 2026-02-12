package com.example.online.middleware.ratelimiter.middleware;

import com.example.online.annotation.RateLimitUser;
import com.example.online.domain.model.User;
import com.example.online.exception.TooManyRequestException;
import com.example.online.middleware.ratelimiter.RateLimiterService;
import com.example.online.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class UserRateLimiter {
    private final RateLimiterService rateLimiterService;

    @Around("@annotation(rateLimitUser)")
    public Object preHandle(ProceedingJoinPoint joinPoint, RateLimitUser rateLimitUser) throws Throwable {
        Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                User user = SecurityUtils.getCurrentUser();
            if (user != null) {
                String id = user.getId().toString();
                String key = "ID:" + id;
                boolean allowed = rateLimiterService.allowedRequest(key, rateLimitUser.capacity(),
                        rateLimitUser.refillTokens(), rateLimitUser.duration());
                if (!allowed) {
                    throw new TooManyRequestException("Too many request!");
                }
            }
        }
        return joinPoint.proceed();
    }
}
