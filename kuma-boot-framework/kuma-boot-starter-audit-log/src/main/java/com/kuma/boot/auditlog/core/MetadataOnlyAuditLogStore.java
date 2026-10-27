package com.kuma.boot.auditlog.core;

import com.kuma.boot.auditlog.model.AuditLogEntry;

/** Metadata-only policy for applications handling credentials and private content. */
public class MetadataOnlyAuditLogStore implements AuditLogStore {
    private final AuditLogStore delegate;
    public MetadataOnlyAuditLogStore() { this(new Slf4jAuditLogStore()); }
    public MetadataOnlyAuditLogStore(AuditLogStore delegate) { this.delegate = delegate; }
    @Override
    public void save(AuditLogEntry entry) {
        entry.setParams(null);
        entry.setResult(null);
        // Raw exceptions may echo passwords, tokens, request payloads or SQL.
        if (entry.getErrorMsg() != null) entry.setErrorMsg("operation_failed");
        delegate.save(entry);
    }
}
