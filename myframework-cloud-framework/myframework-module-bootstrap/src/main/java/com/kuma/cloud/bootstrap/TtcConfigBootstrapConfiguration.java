/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.kuma.cloud.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods=false)
public class KmcConfigBootstrapConfiguration {
    @Bean
    public KmcBootstrapPropertySourceLocator kmcBootstrapPropertySourceLocator() {
        return new KmcBootstrapPropertySourceLocator();
    }
}

