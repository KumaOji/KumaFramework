/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 *  org.springframework.context.annotation.Bean
 */
package com.kuma.boot.actuator.endpoint.kmc.autoconfigure;

import com.kuma.boot.actuator.endpoint.kmc.KmcEndPoint;
import com.kuma.boot.actuator.endpoint.kmc.KmcHealthEndPoint;
import com.kuma.boot.actuator.health.KmcHealthIndicator;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnAvailableEndpoint(endpoint=KmcEndPoint.class)
public class KmcEndPointAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(KmcEndPointAutoConfiguration.class, (String)"kuma-boot-starter-actuator", (String[])new String[0]);
    }

    @Bean
    @ConditionalOnBean(value={KmcHealthIndicator.class})
    public KmcHealthEndPoint kmcHealthEndPoint(KmcHealthIndicator kmcHealthIndicator) {
        return new KmcHealthEndPoint(kmcHealthIndicator);
    }

    @Bean
    public KmcEndPoint kmcEndPoint() {
        return new KmcEndPoint();
    }
}

