/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 */
package com.kuma.cloud.kmc.circuitbreaker.configuration;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.cloud.kmc.circuitbreaker.properties.CircuitbreakerProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(value={CircuitbreakerProperties.class})
@ConditionalOnProperty(prefix="kuma.cloud.kmc.circuitbreaker", name={"enabled"}, havingValue="true")
public class CircuitbreakerAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(CircuitbreakerAutoConfiguration.class, (String)"kuma-cloud-starter-kmc", (String[])new String[0]);
    }
}

