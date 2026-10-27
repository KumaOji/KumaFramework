package com.kuma.boot.auditlog;

import com.kuma.boot.auditlog.annotation.AuditLog;
import com.kuma.boot.auditlog.aop.AuditLogAspect;
import com.kuma.boot.auditlog.autoconfigure.properties.AuditLogProperties;
import com.kuma.boot.auditlog.core.AuditOperatorProvider;
import com.kuma.boot.auditlog.core.MetadataOnlyAuditLogStore;
import com.kuma.boot.auditlog.model.AuditLogEntry;
import com.kuma.boot.common.model.result.Result;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import static org.assertj.core.api.Assertions.*;

class AuditLogAspectTest {
    @Test
    void snapshotsIdentityBeforeLogoutAndUnderstandsExplicitRejection() {
        var entries = new ArrayList<AuditLogEntry>();
        var actor = new AtomicReference<>("authenticated-user");
        var target = new Operations(actor);
        var factory = new AspectJProxyFactory(target);
        factory.addAspect(new AuditLogAspect(entries::add, operator(actor), new AuditLogProperties(), null));
        Operations proxy = factory.getProxy();
        proxy.logout();
        proxy.rejected();
        proxy.noChange();
        assertThat(entries.get(0).getOperatorId()).isEqualTo("authenticated-user");
        assertThat(entries.get(1).isSuccess()).isFalse();
        assertThat(entries.get(2).isSuccess()).isFalse();
        assertThat(entries.get(1).getErrorMsg()).isEqualTo("operation_rejected");
    }

    @Test
    void asynchronousStoreReceivesRequestMdcAndRestoresWorkerMdc() {
        var seen = new ArrayList<String>();
        var entries = new ArrayList<AuditLogEntry>();
        var previous = MDC.getCopyOfContextMap();
        try {
            MDC.clear();
            MDC.put("traceId", "request-trace");
            var factory = new AspectJProxyFactory(new Operations(new AtomicReference<>("user")));
            factory.addAspect(new AuditLogAspect(entry -> { seen.add(MDC.get("traceId")); entries.add(entry); },
                    operator(new AtomicReference<>("user")), new AuditLogProperties(), task -> {
                        MDC.put("traceId", "worker-trace");
                        task.run();
                        assertThat(MDC.get("traceId")).isEqualTo("worker-trace");
                    }));
            ((Operations) factory.getProxy()).noChange();
            assertThat(seen).containsExactly("request-trace");
            assertThat(entries.getFirst().getTraceId()).isEqualTo("request-trace");
        } finally {
            MDC.clear();
            if (previous != null) MDC.setContextMap(previous);
        }
    }

    @Test
    void metadataPolicyRemovesPayloadsAndExceptionMessages() {
        var entries = new ArrayList<AuditLogEntry>();
        var entry = new AuditLogEntry();
        entry.setParams("password=secret");
        entry.setResult("access_token=secret");
        entry.setErrorMsg("SQL rejected credential=secret");
        new MetadataOnlyAuditLogStore(entries::add).save(entry);
        assertThat(entry.getParams()).isNull();
        assertThat(entry.getResult()).isNull();
        assertThat(entry.getErrorMsg()).isEqualTo("operation_failed");
        assertThat(entries).containsExactly(entry);
    }

    private AuditOperatorProvider operator(AtomicReference<String> name) {
        return new AuditOperatorProvider() {
            public String currentOperatorId() { return name.get(); }
            public String currentOperatorName() { return name.get(); }
        };
    }
    public static class Operations {
        private final AtomicReference<String> actor;
        public Operations(AtomicReference<String> actor) { this.actor = actor; }
        @AuditLog public void logout() { actor.set(null); }
        @AuditLog public Result<String> rejected() { return Result.fail("rejected"); }
        @AuditLog public Result<Boolean> noChange() { return Result.success(false); }
    }
}
