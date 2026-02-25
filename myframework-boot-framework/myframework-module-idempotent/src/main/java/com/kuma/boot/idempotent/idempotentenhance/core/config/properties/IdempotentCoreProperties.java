/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.kuma.boot.common.utils.context.ContextUtils
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.jspecify.annotations.NonNull
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.context.ApplicationListener
 *  org.springframework.context.ConfigurableApplicationContext
 *  org.springframework.context.event.ContextRefreshedEvent
 */
package com.kuma.boot.idempotent.idempotentenhance.core.config.properties;

import com.google.common.base.Preconditions;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.concurrent.TimeUnit;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

@ConfigurationProperties(prefix="enhance.idempotent")
public class IdempotentCoreProperties
implements ApplicationListener<ContextRefreshedEvent> {
    public static final String PREFIX = "enhance.idempotent";
    private Long maxProcessTime = -1L;
    private TimeUnit unit = TimeUnit.SECONDS;
    private String namespace;
    private String primaryRepository;

    public void onApplicationEvent(@NonNull ContextRefreshedEvent contextRefreshedEvent) {
        if (StringUtils.isBlank((String)this.namespace)) {
            ConfigurableApplicationContext context = ContextUtils.getApplicationContext();
            this.namespace = context.getEnvironment().getProperty("spring.application.name");
            Preconditions.checkArgument((boolean)StringUtils.isNotBlank((String)this.namespace), (Object)"can not found namespace, please check config [enhance.idempotent.namespace] or [spring.application.name]");
        }
        LogUtils.info((String)"idempotent core namespace is [{}].", (Object[])new Object[]{this.namespace});
    }

    public Long getMaxProcessTime() {
        return this.maxProcessTime;
    }

    public void setMaxProcessTime(Long maxProcessTime) {
        this.maxProcessTime = maxProcessTime;
    }

    public TimeUnit getUnit() {
        return this.unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getPrimaryRepository() {
        return this.primaryRepository;
    }

    public void setPrimaryRepository(String primaryRepository) {
        this.primaryRepository = primaryRepository;
    }
}

