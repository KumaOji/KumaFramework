/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.cloud.stream.binder.BindingCreatedEvent
 *  org.springframework.context.ApplicationListener
 *  org.springframework.context.annotation.Configuration
 */
package com.kuma.cloud.stream.configuration;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.stream.binder.BindingCreatedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration
public class StreamEventListenerAutoConfiguration {

    @Configuration
    public static class BindingCreatedEventListener
    implements ApplicationListener<BindingCreatedEvent> {
        public void onApplicationEvent(BindingCreatedEvent event) {
            LogUtils.info((String)"StreamEventListener ----- BindingCreatedEvent onApplicationEvent {}", (Object[])new Object[]{event});
        }
    }
}

