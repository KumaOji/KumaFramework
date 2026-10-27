package com.kuma.cloud.blog.config;

import com.kuma.cloud.blog.controller.ArticleController;
import com.kuma.cloud.blog.service.ArticleService;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import static org.assertj.core.api.Assertions.*;

class AuditLogConfigurationTest {
    @Test
    void methodPermissionRejectionIsAuditedBeforeBusinessExecution() {
        var records = new java.util.ArrayList<com.kuma.boot.auditlog.model.AuditLogEntry>();
        new ApplicationContextRunner().withPropertyValues("kuma.boot.audit-log.async=false")
                .withConfiguration(org.springframework.boot.autoconfigure.AutoConfigurations.of(
                        com.kuma.boot.auditlog.autoconfigure.AuditLogAutoConfiguration.class))
                .withUserConfiguration(DeniedConfiguration.class)
                .withBean(com.kuma.boot.auditlog.core.AuditLogStore.class, () -> records::add)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThatThrownBy(() -> context.getBean(DeniedOperation.class).write())
                            .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
                    assertThat(records).hasSize(1);
                    assertThat(records.getFirst().isSuccess()).isFalse();
                });
    }

    @org.springframework.context.annotation.Configuration(proxyBeanMethods = false)
    @org.springframework.context.annotation.EnableAspectJAutoProxy
    @org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
    static class DeniedConfiguration {
        @org.springframework.context.annotation.Bean DeniedOperation deniedOperation() { return new DeniedOperation(); }
    }
    public static class DeniedOperation {
        @com.kuma.boot.auditlog.annotation.AuditLog(recordParams = false, recordResult = false)
        @org.springframework.security.access.prepost.PreAuthorize("denyAll()")
        public void write() { throw new AssertionError("Denied operation must not execute"); }
    }

    @Test
    void realControllerProxyUsesTrustedIdentityAndEmitsMetadataOnly() {
        var service = org.mockito.Mockito.mock(ArticleService.class);
        var lines = new CopyOnWriteArrayList<String>();
        var logger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger("com.kuma.boot.auditlog.AUDIT");
        var appender = new ch.qos.logback.core.AppenderBase<ch.qos.logback.classic.spi.ILoggingEvent>() {
            @Override protected void append(ch.qos.logback.classic.spi.ILoggingEvent event) { lines.add(event.getFormattedMessage()); }
        };
        appender.start();
        logger.addAppender(appender);
        var previous = SecurityContextHolder.getContext();
        var request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", "forged-operator");
        request.setRequestURI("/operations");
        var previousRequest = RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        var security = SecurityContextHolder.createEmptyContext();
        security.setAuthentication(UsernamePasswordAuthenticationToken.authenticated("real-operator", "credential", java.util.List.of()));
        SecurityContextHolder.setContext(security);
        try {
            new ApplicationContextRunner().withPropertyValues("kuma.boot.audit-log.async=false")
                    .withUserConfiguration(AuditLogConfiguration.class)
                    .withBean(ArticleService.class, () -> service)
                    .withBean(ArticleController.class, () -> new ArticleController(service))
                    .run(context -> {
                assertThat(context).hasNotFailed();
                var dto = new com.kuma.cloud.blog.domain.dto.ArticleSaveDTO();
                dto.setTitle("private-title");
                dto.setContent("private-body");
                org.mockito.Mockito.when(service.createArticle(org.mockito.ArgumentMatchers.any())).thenReturn(17L);
                org.mockito.Mockito.when(service.publishArticle(17L)).thenReturn(false);
                var controller = context.getBean(ArticleController.class);
                controller.create(dto);
                controller.publish(17L);
                assertThat(lines).hasSize(2);
                assertThat(lines.get(0)).contains("\"operatorId\":\"real-operator\"", "\"success\":true");
                assertThat(lines.get(1)).contains("\"bizId\":\"17\"", "\"success\":false");
                assertThat(String.join("\n", lines)).doesNotContain("private-title", "private-body", "forged-operator");
            });
        } finally {
            SecurityContextHolder.setContext(previous);
            RequestContextHolder.setRequestAttributes(previousRequest);
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @Test
    void canDisableAuditWithoutBreakingAuthenticationCallbackWiring() {
        new ApplicationContextRunner().withPropertyValues("kuma.boot.audit-log.enabled=false")
                .withUserConfiguration(AuditLogConfiguration.class).run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).doesNotHaveBean(com.kuma.boot.auditlog.aop.AuditLogAspect.class);
            assertThat(context).hasSingleBean(com.kuma.boot.auditlog.core.ServletAuthenticationAuditRecorder.class);
        });
    }
}
