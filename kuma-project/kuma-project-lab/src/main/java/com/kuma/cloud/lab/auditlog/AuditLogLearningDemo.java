package com.kuma.cloud.lab.auditlog;

import com.kuma.boot.auditlog.annotation.AuditLog;
import com.kuma.boot.auditlog.autoconfigure.AuditLogAutoConfiguration;
import com.kuma.boot.auditlog.core.AuditLogStore;
import com.kuma.boot.auditlog.core.AuditOperatorProvider;
import com.kuma.boot.auditlog.core.Slf4jAuditLogStore;
import com.kuma.boot.auditlog.enums.OperateType;
import com.kuma.boot.auditlog.model.AuditLogEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.MapPropertySource;
import tools.jackson.databind.json.JsonMapper;

/** Uses the actual starter and Spring proxy, with per-run records and no external storage. */
public final class AuditLogLearningDemo {
    public record Evidence(String method, String operatorId, String operatorName, String type,
            String module, String bizId, String description, boolean success, String error,
            String params, String result, long costMillis) {
        static Evidence from(AuditLogEntry entry) {
            return new Evidence(entry.getMethod(), entry.getOperatorId(), entry.getOperatorName(),
                    entry.getType().name(), entry.getModule(), entry.getBizId(), entry.getDescription(),
                    entry.isSuccess(), entry.getErrorMsg(), entry.getParams(), entry.getResult(), entry.getCostMillis());
        }
    }
    public record Step(String name, String explanation, int recordsAdded) {}
    public record Report(String lesson, List<Step> steps, List<Evidence> records, Map<String, Boolean> checks) {}
    private AuditLogLearningDemo() {}

    public static void main(String[] args) throws Exception {
        System.out.println(JsonMapper.builder().build().writeValueAsString(run(args.length == 0 ? "all" : args[0])));
    }

    public static Report run(String lesson) {
        if (!List.of("all", "success", "failure", "content", "proxy", "storage").contains(lesson)) {
            throw new IllegalArgumentException("Choose all, success, failure, content, proxy or storage");
        }
        var steps = new ArrayList<Step>();
        var checks = new LinkedHashMap<String, Boolean>();
        try (var context = new AnnotationConfigApplicationContext()) {
            context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("audit-lesson", Map.of(
                    "kuma.boot.audit-log.enabled", true, "kuma.boot.audit-log.async", false,
                    "kuma.boot.audit-log.max-content-length", 160)));
            context.register(LessonConfiguration.class, AuditLogAutoConfiguration.class);
            context.refresh();
            var service = context.getBean(LessonService.class);
            var store = context.getBean(RecordingStore.class);
            check(checks, "springProxyEnabled", org.springframework.aop.support.AopUtils.isAopProxy(service));

            if (selected(lesson, "success")) {
                String result = service.confirm("ORDER-001");
                Evidence record = store.last();
                check(checks, "businessResultPreserved", result.equals("CONFIRMED:ORDER-001"));
                check(checks, "successAndSpelCaptured", record.success() && "ORDER-001".equals(record.bizId())
                        && "确认订单:ORDER-001".equals(record.description()));
                check(checks, "operatorAndTimingCaptured", "lab-user".equals(record.operatorId())
                        && "Lab Learner".equals(record.operatorName()) && record.costMillis() >= 0);
                steps.add(new Step("成功审计", "真实代理执行 @AuditLog；操作人来自教学 Provider，SpEL 从方法参数求值。", 1));
            }
            if (selected(lesson, "failure")) {
                RuntimeException caught = null;
                try { service.reject("ORDER-002"); } catch (IllegalStateException error) { caught = error; }
                Evidence record = store.last();
                check(checks, "originalBusinessExceptionPreserved", caught == service.rejection());
                check(checks, "failedOperationCaptured", !record.success() && "订单已经确认".equals(record.error())
                        && record.result() == null && "ORDER-002".equals(record.bizId()));
                steps.add(new Step("失败审计", "切面记录 success=false 和异常消息，并继续抛出原业务异常；实验捕获它以继续展示报告。", 1));
            }
            if (selected(lesson, "content")) {
                service.changeSecret("USER-001", "LAB-ONLY-SECRET");
                Evidence hidden = store.last();
                check(checks, "sensitivePayloadDisabled", hidden.params() == null && hidden.result() == null);
                service.largePayload("x".repeat(400));
                Evidence truncated = store.last();
                check(checks, "oversizedPayloadTruncated", truncated.params().endsWith("...(truncated)")
                        && truncated.result().endsWith("...(truncated)")
                        && truncated.params().length() == 160 + "...(truncated)".length());
                steps.add(new Step("内容策略", "关闭敏感方法的入参与出参记录；普通大文本按160字符加截断后缀保存，截断不等于脱敏。", 2));
            }
            if (selected(lesson, "proxy")) {
                int before = store.records.size();
                service.selfInvoke("ORDER-003");
                check(checks, "selfInvocationBypassesAudit", store.records.size() == before);
                service.confirm("ORDER-003");
                check(checks, "proxyInvocationProducesAudit", store.records.size() == before + 1);
                steps.add(new Step("代理边界", "同对象 this 调用绕过代理，无新增审计；从注入的代理调用相同方法可产生审计。", 1));
            }
            if (selected(lesson, "storage")) {
                int before = store.records.size();
                store.failWrites = true;
                String result = service.confirm("ORDER-004");
                store.failWrites = false;
                check(checks, "auditStorageFailureDoesNotFailBusiness", result.equals("CONFIRMED:ORDER-004")
                        && store.records.size() == before && store.failedWrites == 1);
                steps.add(new Step("存储故障", "模拟 AuditLogStore 故障，框架记录诊断日志而业务仍成功；这也意味着审计记录可能丢失。", 0));
            }
            return new Report(lesson, List.copyOf(steps), List.copyOf(store.records),
                    Collections.unmodifiableMap(new LinkedHashMap<>(checks)));
        }
    }

    private static boolean selected(String lesson, String name) { return lesson.equals("all") || lesson.equals(name); }
    private static void check(Map<String, Boolean> checks, String name, boolean passed) {
        checks.put(name, passed);
        if (!passed) throw new IllegalStateException("Audit lesson check failed: " + name);
    }

    // Registered only by run(); avoid component scanning into the host Lab application.
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    public static class LessonConfiguration {
        @Bean RecordingStore lessonAuditStore() { return new RecordingStore(); }
        @Bean LessonService lessonService() { return new LessonService(); }
        @Bean AuditOperatorProvider lessonOperator() {
            return new AuditOperatorProvider() {
                public String currentOperatorId() { return "lab-user"; }
                public String currentOperatorName() { return "Lab Learner"; }
            };
        }
    }

    public static class RecordingStore implements AuditLogStore {
        private final List<Evidence> records = new ArrayList<>();
        private final Slf4jAuditLogStore log = new Slf4jAuditLogStore();
        private boolean failWrites;
        private int failedWrites;
        @Override public void save(AuditLogEntry entry) {
            if (failWrites) { failedWrites++; throw new IllegalStateException("模拟审计存储不可用"); }
            records.add(Evidence.from(entry));
            log.save(entry);
        }
        Evidence last() { return records.getLast(); }
    }

    public static class LessonService {
        private final IllegalStateException rejection = new IllegalStateException("订单已经确认");
        @AuditLog(module = "lab.audit.orders", type = OperateType.UPDATE,
                bizId = "#id", description = "'确认订单:' + #id")
        public String confirm(String id) { return "CONFIRMED:" + id; }
        @AuditLog(module = "lab.audit.orders", type = OperateType.UPDATE, bizId = "#id", description = "确认订单失败")
        public String reject(String id) { throw rejection; }
        @AuditLog(module = "lab.audit.users", type = OperateType.UPDATE, bizId = "#id",
                description = "更新教学密钥", recordParams = false, recordResult = false)
        public String changeSecret(String id, String secret) { return "replaced:" + secret; }
        @AuditLog(module = "lab.audit.content", description = "大文本截断")
        public String largePayload(String text) { return text; }
        public String selfInvoke(String id) { return confirm(id); }
        public IllegalStateException rejection() { return rejection; }
    }
}
