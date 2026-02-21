/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.core.startup.StartupReporter
 *  org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint
 *  org.springframework.boot.actuate.autoconfigure.startup.StartupEndpointAutoConfiguration
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.context.annotation.Bean
 */
package com.kuma.boot.actuator.endpoint.startup.autoconfigure;

import com.kuma.boot.actuator.endpoint.startup.KmcApplicationStartupEndpoint;
import com.kuma.boot.core.startup.StartupReporter;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.autoconfigure.startup.StartupEndpointAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(before={StartupEndpointAutoConfiguration.class})
@ConditionalOnBean(value={StartupReporter.class})
@ConditionalOnAvailableEndpoint(endpoint=KmcApplicationStartupEndpoint.class)
public class KmcApplicationStartupEndPointAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public KmcApplicationStartupEndpoint kmcApplicationStartupEndpoint(StartupReporter startupReporter) {
        return new KmcApplicationStartupEndpoint(startupReporter);
    }
}

