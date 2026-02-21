/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 */
package com.kuma.boot.actuator.mbean.autoconfigure;

import com.kuma.boot.actuator.mbean.KmcMBeanRegistrar;
import com.kuma.boot.actuator.mbean.KmcManagedResource;
import com.kuma.boot.actuator.mbean.autoconfigure.properties.ManagedResourceProperties;
import com.kuma.boot.common.utils.log.LogUtils;
import javax.management.MalformedObjectNameException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(value={ManagedResourceProperties.class})
@ConditionalOnProperty(prefix="kuma.boot.actuator.managedresource", name={"enabled"}, havingValue="true", matchIfMissing=true)
public class ManagedResourceAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(ManagedResourceAutoConfiguration.class, (String)"kuma-boot-starter-actuator", (String[])new String[0]);
    }

    @Bean
    public KmcManagedResource kmcManagedResource() {
        return new KmcManagedResource();
    }

    @Bean
    public KmcMBeanRegistrar kmcMbeanRegistrar() throws MalformedObjectNameException {
        return new KmcMBeanRegistrar();
    }
}

