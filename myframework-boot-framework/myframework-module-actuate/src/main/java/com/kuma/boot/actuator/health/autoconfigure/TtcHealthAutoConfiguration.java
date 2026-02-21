/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.health.autoconfigure.contributor.ConditionalOnEnabledHealthIndicator
 *  org.springframework.context.annotation.Bean
 */
package com.kuma.boot.actuator.health.autoconfigure;

import com.kuma.boot.actuator.health.KmcHealthIndicator;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.health.autoconfigure.contributor.ConditionalOnEnabledHealthIndicator;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnEnabledHealthIndicator(value="kmc")
public class KmcHealthAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(KmcHealthAutoConfiguration.class, (String)"kuma-boot-starter-actuator", (String[])new String[0]);
    }

    @Bean
    public KmcHealthIndicator kmcHealthIndicator() {
        return new KmcHealthIndicator();
    }
}

