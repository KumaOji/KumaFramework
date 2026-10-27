package com.kuma.cloud.uaa.config;

import com.kuma.cloud.uaa.controller.UserController;
import com.kuma.cloud.uaa.service.UaaUserService;
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
    void realControllerProxyUsesTrustedIdentityAndEmitsMetadataOnly() {
        var service = org.mockito.Mockito.mock(UaaUserService.class);
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
                    .withBean(UaaUserService.class, () -> service)
                    .withBean(UserController.class, () -> new UserController(service))
                    .run(context -> {
                assertThat(context).hasNotFailed();
                org.mockito.Mockito.doThrow(new IllegalStateException("password=must-not-leak"))
                        .when(service).resetPassword(17L, "must-not-leak");
                var controller = context.getBean(UserController.class);
                org.assertj.core.api.Assertions.assertThatThrownBy(() -> controller.resetPassword(17L, "must-not-leak"))
                        .isInstanceOf(IllegalStateException.class);
                assertThat(lines).hasSize(1);
                assertThat(lines.getFirst()).contains("\"operatorId\":\"real-operator\"", "\"bizId\":\"17\"", "\"success\":false");
                assertThat(lines.getFirst()).doesNotContain("must-not-leak", "forged-operator");
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
