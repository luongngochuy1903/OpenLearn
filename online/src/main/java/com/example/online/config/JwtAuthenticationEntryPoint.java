package com.example.online.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        String code = (String) request.getAttribute("auth_error");
        if (code == null) code = "UNAUTHORIZED";

        String message;
        switch (code) {
            case "ACCESS_TOKEN_EXPIRED" -> {
                message = "Access token expired";
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            case "TOO_MANY_REQUEST" -> {
                message = "Too many request!";
                response.setStatus(429);
            }
            default -> {
                message = "You have to login";
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        };
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write("""
        {
          "status": %s,
          "error": "Unauthorized",
          "code": "%s",
          "message": "%s",
          "path": "%s",
          "timestamp": "%s"
        }
        """.formatted(
                response.getStatus(),
                code,
                message,
                request.getRequestURI(),
                LocalDateTime.now()
        ));
    }
}
