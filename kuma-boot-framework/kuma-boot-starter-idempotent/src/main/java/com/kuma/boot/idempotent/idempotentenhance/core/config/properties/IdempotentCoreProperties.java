package com.kuma.boot.idempotent.idempotentenhance.core.config.properties;

import com.google.common.base.Preconditions;
import com.kuma.boot.core.utils.context.ContextUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.TimeUnit;

/**
 * 幂等核心properties
 *
 * @author wenpan 2023/01/04 21:57
 */
@ConfigurationProperties(prefix = IdempotentCoreProperties.PREFIX)
public class IdempotentCoreProperties implements ApplicationListener<ContextRefreshedEvent> {

    public static final String PREFIX = "enhance.idempotent";

    /**
     * 业务最大处理时间，默认值为-1，表示失败数据永远不自愈，等待人工处理
     */
    private Long maxProcessTime = -1L;

    /**
     * 最大处理时间单位（默认秒）
     */
    private TimeUnit unit = TimeUnit.SECONDS;

    /**
     * 命名空间（用于应用隔离）
     */
    private String namespace;

    /**
     * primary idempotent repository
     */
    private String primaryRepository;

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent contextRefreshedEvent) {
        if (StringUtils.isBlank(namespace)) {
            ApplicationContext context = ContextUtils.getApplicationContext();
            namespace = context.getEnvironment().getProperty("spring.application.name");
            Preconditions.checkArgument(StringUtils.isNotBlank(namespace), "can not found namespace," +
                    " please check config [enhance.idempotent.namespace] or [spring.application.name]");
        }
        LogUtils.info("idempotent core namespace is [{}].", namespace);
    }

    public Long getMaxProcessTime() {
        return maxProcessTime;
    }

    public void setMaxProcessTime( Long maxProcessTime ) {
        this.maxProcessTime = maxProcessTime;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit( TimeUnit unit ) {
        this.unit = unit;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace( String namespace ) {
        this.namespace = namespace;
    }

    public String getPrimaryRepository() {
        return primaryRepository;
    }

    public void setPrimaryRepository( String primaryRepository ) {
        this.primaryRepository = primaryRepository;
    }
}
