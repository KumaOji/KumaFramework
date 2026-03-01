/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.cloud.nacos.NacosDiscoveryProperties
 *  com.alibaba.cloud.nacos.NacosServiceManager
 *  com.alibaba.cloud.nacos.discovery.NacosDiscoveryClient
 *  com.alibaba.nacos.api.naming.listener.NamingEvent
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.context.annotation.Configuration
 */
package com.kuma.cloud.alibaba.nacos.listener;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryClient;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

public class NacosServiceListener
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(NacosServiceListener.class, (String)"kuma-boot-starter-core", (String[])new String[0]);
    }

    @Configuration
    public static class KmcNacosServiceListener
    implements InitializingBean {
        private final NacosServiceManager nacosServiceManager;
        private final NacosDiscoveryProperties properties;
        private final NacosDiscoveryClient discoveryClient;

        public KmcNacosServiceListener(NacosServiceManager nacosServiceManager, NacosDiscoveryProperties properties, NacosDiscoveryClient discoveryClient) {
            this.nacosServiceManager = nacosServiceManager;
            this.properties = properties;
            this.discoveryClient = discoveryClient;
        }

        public void afterPropertiesSet() throws Exception {
            this.nacosServiceManager.getNamingService().subscribe(this.properties.getService(), this.properties.getGroup(), Collections.singletonList(this.properties.getClusterName()), event -> {
                if (event instanceof NamingEvent) {
                    List instances = ((NamingEvent)event).getInstances();
                    LogUtils.info((String)"", (Object[])new Object[0]);
                }
            });
            List services = this.discoveryClient.getServices();
            if (!services.isEmpty()) {
                for (String service : services) {
                    this.nacosServiceManager.getNamingService().subscribe(service, this.properties.getGroup(), List.of(this.properties.getClusterName()), event -> {
                        if (event instanceof NamingEvent) {
                            List instances = ((NamingEvent)event).getInstances();
                            LogUtils.info((String)"", (Object[])new Object[0]);
                        }
                    });
                }
            }
        }
    }
}

