package com.example.online.monitoring.auditlog.impl;

import com.example.online.domain.model.AuditLog;
import com.example.online.domain.model.User;
import com.example.online.monitoring.auditlog.AuditLogService;
import com.example.online.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {
    private final AuditLogRepository auditLogRepository;
    @Override
    public void saveToAudit(AuditLog auditLog) {
        auditLogRepository.save(auditLog);

    }
}
