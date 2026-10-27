package com.kuma.boot.auditlog.core;

import com.kuma.boot.auditlog.enums.OperateType;
import com.kuma.boot.auditlog.model.AuditLogEntry;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/** Records explicit authentication outcomes, including callbacks that return normally on failure. */
public class ServletAuthenticationAuditRecorder {
    private final AuditLogStore store;
    public ServletAuthenticationAuditRecorder(AuditLogStore store) { this.store = store; }
    public void record(String module, String actor, boolean success, String outcome, HttpServletRequest request) {
        record(module, actor, success, outcome, request, OperateType.LOGIN);
    }
    public void logout(String module, String actor, HttpServletRequest request) {
        record(module, actor, true, "logout", request, OperateType.LOGOUT);
    }
    private void record(String module, String actor, boolean success, String outcome, HttpServletRequest request,
            OperateType type) {
        try {
            var entry = new AuditLogEntry();
            entry.setModule(module);
            entry.setType(type);
            entry.setOperatorId(actor);
            entry.setOperatorName(actor);
            entry.setSuccess(success);
            entry.setDescription(outcome);
            entry.setTraceId(MDC.get("traceId") != null ? MDC.get("traceId") : MDC.get("kmc-trace-id"));
            entry.setOperateTime(LocalDateTime.now());
            if (request != null) {
                entry.setRequestUri(request.getRequestURI());
                entry.setHttpMethod(request.getMethod());
                entry.setClientIp(request.getRemoteAddr());
            }
            store.save(entry);
        } catch (RuntimeException ignored) {
            LoggerFactory.getLogger(getClass()).warn("Authentication audit could not be recorded");
        }
    }
}
