package com.kuma.cloud.blog.config;

import com.kuma.boot.springdoc.autoconfigure.SpringdocAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/** Explicitly loads the Kuma starter features used by this application. */
@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        SpringdocAutoConfiguration.class
})
public class SpringdocConfiguration {
}
