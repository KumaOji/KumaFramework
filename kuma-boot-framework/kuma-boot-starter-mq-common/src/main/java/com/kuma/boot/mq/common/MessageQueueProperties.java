package com.kuma.boot.mq.common;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kuma.boot.mq")
public class MessageQueueProperties {
    public static final String PREFIX = "kuma.boot.mq";
    public static final String ENABLED = "kuma.boot.mq.enabled";
    private String type;
    private boolean enabled;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
