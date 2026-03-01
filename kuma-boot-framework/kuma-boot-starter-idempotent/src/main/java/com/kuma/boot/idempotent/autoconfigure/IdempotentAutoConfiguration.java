/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.lock.support.DistributedLock
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 */
package com.kuma.boot.idempotent.autoconfigure;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.idempotent.aop.IdempotentAspect;
import com.kuma.boot.idempotent.autoconfigure.properties.IdempotentProperties;
import com.kuma.boot.lock.support.DistributedLock;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(value={IdempotentProperties.class})
@ConditionalOnProperty(prefix="kuma.boot.idempotent", name={"enabled"}, havingValue="true", matchIfMissing=true)
public class IdempotentAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(IdempotentAutoConfiguration.class, (String)"kuma-boot-starter-idempotent", (String[])new String[0]);
    }

    @Bean
    @ConditionalOnBean
    public IdempotentAspect idempotentAspect(DistributedLock distributedLock) {
        return new IdempotentAspect(distributedLock);
    }
}

