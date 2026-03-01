/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  com.kuma.boot.sentinel.autoconfigure.SentinelAutoConfiguration
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 */
package com.kuma.cloud.sentinel.configuration;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.sentinel.autoconfigure.SentinelAutoConfiguration;
import com.kuma.cloud.sentinel.properties.SentinelCloudProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration(after={SentinelAutoConfiguration.class})
@EnableConfigurationProperties(value={SentinelCloudProperties.class})
@ConditionalOnProperty(prefix="kuma.cloud.alibaba.sentinel", name={"enabled"}, havingValue="true")
public class SentinelCloudAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(SentinelCloudAutoConfiguration.class, (String)"kuma-cloud-starter-sentinel", (String[])new String[0]);
    }
}

