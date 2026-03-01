/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.cloud.nacos.event.NacosDiscoveryInfoChangedEvent
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.ApplicationListener
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.kuma.cloud.alibaba.nacos.configuration;

import com.alibaba.cloud.nacos.event.NacosDiscoveryInfoChangedEvent;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.cloud.alibaba.nacos.listener.NacosConfigListener;
import com.kuma.cloud.alibaba.nacos.listener.NacosServiceListener;
import com.kuma.cloud.alibaba.nacos.properties.NacosCloudProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ConditionalOnProperty(prefix="kuma.cloud.alibaba.nacos", name={"enabled"}, havingValue="true")
@Import(value={NacosConfigListener.class, NacosServiceListener.class})
@EnableConfigurationProperties(value={NacosCloudProperties.class})
public class NacosEventListenerAutoConfiguration {

    @Configuration
    public static class NacosDiscoveryInfoChangedEventListener
    implements ApplicationListener<NacosDiscoveryInfoChangedEvent> {
        public void onApplicationEvent(NacosDiscoveryInfoChangedEvent event) {
            LogUtils.info((String)"NacosEventListener ----- NacosDiscoveryInfoChangedEvent onApplicationEvent {}", (Object[])new Object[]{event});
        }
    }
}

