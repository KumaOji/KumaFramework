/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.context.annotation.ImportBeanDefinitionRegistrar
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.util.ClassUtils
 */
package com.kuma.boot.web.httpexchange;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

class ExchangeClientsRegistrar
implements ImportBeanDefinitionRegistrar {
    ExchangeClientsRegistrar() {
    }

    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        Map attrs = Optional.ofNullable(metadata.getAnnotationAttributes(EnableExchangeClients.class.getName())).orElse(Map.of());
        String[] basePackages = Optional.ofNullable((String[])attrs.get("value")).orElseGet(() -> new String[0]);
        Class[] clientClasses = Optional.ofNullable((Class[])attrs.get("clients")).orElseGet(() -> new Class[0]);
        HttpClientBeanDefinitionRegistry.scanInfo.clients.addAll(List.of(clientClasses));
        HttpClientBeanDefinitionRegistry.scanInfo.basePackages.addAll(List.of(basePackages));
        if (basePackages.length == 0 && clientClasses.length == 0) {
            HttpClientBeanDefinitionRegistry.scanInfo.basePackages.add(ClassUtils.getPackageName((String)metadata.getClassName()));
        }
    }
}

