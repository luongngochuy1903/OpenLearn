package com.example.online.monitoring.auditlog;

import com.example.online.domain.model.AuditLog;

public interface AuditLogService {
    public void saveToAudit(AuditLog auditLog);
}
