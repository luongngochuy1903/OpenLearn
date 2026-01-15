package com.example.online.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException ex
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write("""
        {
          "status": 403,
          "error": "Forbidden",
          "message": "Bạn không có quyền truy cập",
          "path": "%s",
          "timestamp": "%s"
        }
        """.formatted(
                request.getRequestURI(),
                LocalDateTime.now()
        ));
    }
}
