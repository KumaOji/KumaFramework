package com.kuma.boot.web.annotation;

import com.kuma.boot.security.spring.annotation.EnableOauth2ResourceServer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/** Select optional application features without registering starters globally. */
public class KumaBootApplicationImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        var attributes = metadata.getAnnotationAttributes(KumaBootApplication.class.getName());
        return attributes != null && Boolean.TRUE.equals(attributes.get("oauth2ResourceServer"))
                ? new String[] {ResourceServerConfiguration.class.getName()} : new String[0];
    }

    @Configuration(proxyBeanMethods = false)
    @EnableOauth2ResourceServer
    static class ResourceServerConfiguration {}
}
