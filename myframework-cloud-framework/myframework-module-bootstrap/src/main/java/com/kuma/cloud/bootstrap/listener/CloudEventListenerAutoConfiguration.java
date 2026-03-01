/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.cloud.client.discovery.event.HeartbeatEvent
 *  org.springframework.cloud.client.discovery.event.InstancePreRegisteredEvent
 *  org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent
 *  org.springframework.cloud.context.environment.EnvironmentChangeEvent
 *  org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent
 *  org.springframework.context.ApplicationListener
 *  org.springframework.context.annotation.Configuration
 */
package com.kuma.cloud.bootstrap.listener;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.cloud.client.discovery.event.InstancePreRegisteredEvent;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration
public class CloudEventListenerAutoConfiguration
implements InitializingBean {
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(CloudEventListenerAutoConfiguration.class, (String)"kuma-cloud-starter-bootstrap", (String[])new String[0]);
    }

    @Configuration
    public static class InstancePreRegisteredEventEventListener
    implements ApplicationListener<InstancePreRegisteredEvent> {
        public void onApplicationEvent(InstancePreRegisteredEvent event) {
            LogUtils.info((String)"CloudEventListener ----- InstancePreRegisteredEvent onApplicationEvent {}", (Object[])new Object[]{event});
        }
    }

    @Configuration
    public static class InstanceRegisteredEventListener
    implements ApplicationListener<InstanceRegisteredEvent<Object>> {
        public void onApplicationEvent(InstanceRegisteredEvent<Object> event) {
            LogUtils.info((String)"CloudEventListener ----- InstanceRegisteredEvent onApplicationEvent {}", (Object[])new Object[]{event});
        }
    }

    @Configuration
    public static class HeartbeatEventListener
    implements ApplicationListener<HeartbeatEvent> {
        public void onApplicationEvent(HeartbeatEvent event) {
        }
    }

    @Configuration
    public static class EnvironmentChangeEventListener
    implements ApplicationListener<EnvironmentChangeEvent> {
        public void onApplicationEvent(EnvironmentChangeEvent event) {
            LogUtils.info((String)"CloudEventListener ----- EnvironmentChangeEvent onApplicationEvent {}", (Object[])new Object[]{event});
        }
    }

    @Configuration
    public static class RefreshScopeRefreshedEventListener
    implements ApplicationListener<RefreshScopeRefreshedEvent> {
        public void onApplicationEvent(RefreshScopeRefreshedEvent event) {
            LogUtils.info((String)"CloudEventListener ----- RefreshScopeRefreshedEvent onApplicationEvent {}", (Object[])new Object[]{event});
        }
    }
}

