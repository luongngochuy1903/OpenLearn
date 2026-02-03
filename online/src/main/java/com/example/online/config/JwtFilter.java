package com.example.online.config;

import com.example.online.authentication.jwt.service.JwtService;
import com.example.online.domain.model.User;
import com.example.online.exception.ResourceNotFoundException;
import com.example.online.key.RedisKey;
import com.example.online.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final StringRedisTemplate stringRedisTemplate;
    private final UserRepository userRepository;

    @Override
    public void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        String path = request.getServletPath();
        if (path.startsWith("/api/v1/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }


        String jti = jwtService.extractJti(jwt);
        Long id = Long.valueOf(jwtService.extractUsername(jwt));
        Date iat = jwtService.extractClaim(jwt, Claims::getIssuedAt);
        long iatEpoch = iat.getTime() / 1000;

        // Check global revoke
        String validAfterStr = stringRedisTemplate.opsForValue().get(RedisKey.userValidAfter(id));
        if (validAfterStr != null) {
            long validAfterEpoch = Long.parseLong(validAfterStr);
            if (iatEpoch < validAfterEpoch) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 2) Check blacklist jti
        Boolean blacklisted = stringRedisTemplate.hasKey(RedisKey.jwtBlacklist(jti));
        if (blacklisted) {
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null){
            User userDetails = userRepository.findById(id).orElse(null);
            if (userDetails != null){
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authToken);
                    SecurityContextHolder.setContext(context);
                }
                else{
                    request.setAttribute("auth_error", "ACCESS_TOKEN_EXPIRED");
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
