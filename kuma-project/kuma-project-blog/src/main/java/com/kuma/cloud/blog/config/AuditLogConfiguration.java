package com.kuma.cloud.blog.config;

import com.kuma.boot.auditlog.autoconfigure.AuditLogAutoConfiguration;
import com.kuma.boot.auditlog.core.AuditLogStore;
import com.kuma.boot.auditlog.core.AuditOperatorProvider;
import com.kuma.boot.auditlog.core.MetadataOnlyAuditLogStore;
import com.kuma.boot.auditlog.core.ServletAuthenticationAuditRecorder;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration(proxyBeanMethods = false)
@EnableAspectJAutoProxy
@ImportAutoConfiguration(AuditLogAutoConfiguration.class)
public class AuditLogConfiguration {
    @Bean
    AuditLogStore auditLogStore(Environment environment) {
        return environment.getProperty("kuma.boot.audit-log.enabled", Boolean.class, true)
                ? new MetadataOnlyAuditLogStore() : entry -> {};
    }

    @Bean
    AuditOperatorProvider auditOperatorProvider() {
        return new AuditOperatorProvider() {
            public String currentOperatorId() { return authenticatedName(); }
            public String currentOperatorName() { return authenticatedName(); }
            private String authenticatedName() {
                var authentication = SecurityContextHolder.getContext().getAuthentication();
                return authentication != null && authentication.isAuthenticated()
                        && !(authentication instanceof AnonymousAuthenticationToken) ? authentication.getName() : null;
            }
        };
    }

    @Bean
    ServletAuthenticationAuditRecorder authenticationAuditRecorder(AuditLogStore store) {
        return new ServletAuthenticationAuditRecorder(store);
    }
}
