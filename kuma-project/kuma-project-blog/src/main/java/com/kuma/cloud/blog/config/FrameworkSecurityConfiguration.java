package com.kuma.cloud.blog.config;

import com.kuma.boot.security.spring.access.expression.AuthorizeCheckService;
import com.kuma.boot.security.spring.autoconfigure.PropertiesAutoConfiguration;
import com.kuma.boot.security.spring.autoconfigure.SecurityAccessAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/** Explicitly loads the Kuma starter features used by this application. */
@Configuration(proxyBeanMethods = false)
@Import(AuthorizeCheckService.class)
@ImportAutoConfiguration({
        PropertiesAutoConfiguration.class,
        SecurityAccessAutoConfiguration.class
})
public class FrameworkSecurityConfiguration {
}
