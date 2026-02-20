package com.kuma.boot.core.chain.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 责任链处理者配置属性类
 */
@ConfigurationProperties(prefix = "com.kuma.boot.core.chain")
public class ChainHandlerProperties {
    private int corePoolSize = 5;
    private int maxPoolSize = 10;
    private int keepAliveTime = 60;
    private int queueCapacity = 100;

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(int keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }
}
