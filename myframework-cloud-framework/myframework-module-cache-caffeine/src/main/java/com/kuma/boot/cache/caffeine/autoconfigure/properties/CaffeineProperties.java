/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.cloud.context.config.annotation.RefreshScope
 */
package com.kuma.boot.cache.caffeine.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(value="kuma.boot.cache.caffeine")
public class CaffeineProperties {
    public static final String PREFIX = "kuma.boot.cache.caffeine";
    private boolean enabled = true;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

