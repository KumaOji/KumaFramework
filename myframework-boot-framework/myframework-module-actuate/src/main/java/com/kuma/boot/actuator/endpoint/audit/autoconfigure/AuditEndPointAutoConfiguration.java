/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.actuate.audit.AuditEventsEndpoint
 *  org.springframework.boot.actuate.audit.InMemoryAuditEventRepository
 *  org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.context.annotation.Bean
 */
package com.kuma.boot.actuator.endpoint.audit.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.actuate.audit.AuditEventsEndpoint;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnAvailableEndpoint(value=AuditEventsEndpoint.class)
public class AuditEndPointAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(AuditEndPointAutoConfiguration.class, (String)"kuma-boot-starter-actuator", (String[])new String[0]);
    }

    @Bean
    public InMemoryAuditEventRepository kmcInMemoryAuditEventRepository() {
        return new InMemoryAuditEventRepository();
    }
}

