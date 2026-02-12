package com.example.online.annotation.aspect;

import com.example.online.annotation.AuditLog;
import com.example.online.domain.model.User;
import com.example.online.monitoring.auditlog.AuditLogService;
import com.example.online.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {
    private final AuditLogService auditLogService;

    @AfterReturning("@annotation(auditLog)")
    public void getAuditLogAfterMethodAspect(JoinPoint joinPoint, AuditLog auditLog){
        saveAudit(joinPoint, auditLog);
    }

    private void saveAudit(JoinPoint joinPoint, AuditLog auditLog) {
        User user = SecurityUtils.getCurrentUser();

        HttpServletRequest request =
                ((ServletRequestAttributes)
                        RequestContextHolder.getRequestAttributes())
                        .getRequest();
        if (user != null) {
            com.example.online.domain.model.AuditLog entity = com.example.online.domain.model.AuditLog.builder()
                    .actorId(user.getId())
                    .methodName(joinPoint.getSignature().getName())
                    .actorUsername(user.getLastName())
                    .actorRole(user.getRole())
                    .action(auditLog.action())
                    .description(auditLog.description())
                    .endpoint(request.getRequestURI())
                    .httpMethod(request.getMethod())
                    .ipAddress(request.getRemoteAddr())
                    .createdAt(LocalDateTime.now())
                    .build();

            auditLogService.saveToAudit(entity);
        }
    }
}
