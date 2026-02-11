package com.example.online.middleware.ratelimiter.middleware;

import com.example.online.exception.TooManyRequestException;
import com.example.online.middleware.ratelimiter.RateLimiterService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class IpRateLimiterFilter extends OncePerRequestFilter {
    private final RateLimiterService rateLimiterService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ip = request.getRemoteAddr(); // In case of using load balancer, change to header "X-Forwarded-For"
        String key = "IP:" + ip;
        System.out.println("My ip " + key);
        boolean allowed = rateLimiterService.allowedRequest(key, 5, 1, 1);
        System.out.println("allowed " + allowed);
        if (!allowed){
            request.setAttribute("auth_error", "TOO_MANY_REQUEST");
        }
        filterChain.doFilter(request, response);
    }
}
