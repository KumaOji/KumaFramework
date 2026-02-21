/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.core.startup.StartupReporter
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 *  org.springframework.context.annotation.Bean
 */
package com.kuma.boot.actuator.endpoint.startup.autoconfigure;

import com.kuma.boot.actuator.endpoint.startup.KmcStartupEndpoint;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.core.startup.StartupReporter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnAvailableEndpoint(endpoint=KmcStartupEndpoint.class)
public class KmcStartupEndpointAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(KmcStartupEndpointAutoConfiguration.class, (String)"kuma-boot-starter-actuator", (String[])new String[0]);
    }

    @Bean
    @ConditionalOnBean(value={StartupReporter.class})
    public KmcStartupEndpoint kmcStartupEndpoint(StartupReporter startupReporter) {
        return new KmcStartupEndpoint(startupReporter);
    }
}

