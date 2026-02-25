/*
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.cloud.context.config.annotation.RefreshScope
 */
package com.kuma.boot.lock.autoconfigure.properties;

import com.kuma.boot.lock.enums.LockTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(prefix="kuma.boot.lock")
public class LockProperties {
    public static final String PREFIX = "kuma.boot.lock";
    private boolean enabled = true;
    private LockTypeEnum type = LockTypeEnum.LOCAL;

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public LockTypeEnum getType() {
        return this.type;
    }

    public void setType(LockTypeEnum type) {
        this.type = type;
    }
}

