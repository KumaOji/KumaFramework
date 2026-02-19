/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.cloud.context.config.annotation.RefreshScope
 */
package com.kuma.boot.web.request.properties;

import com.kuma.boot.web.request.enums.RequestLoggerTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(prefix="kuma.boot.web.request")
public class RequestLoggerProperties {
    public static final String PREFIX = "kuma.boot.web.request";
    private Boolean enabled = true;
    private RequestLoggerTypeEnum[] types = new RequestLoggerTypeEnum[]{RequestLoggerTypeEnum.LOGGER};

    public Boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public RequestLoggerTypeEnum[] getTypes() {
        if (this.types == null || this.types.length == 0) {
            return new RequestLoggerTypeEnum[]{RequestLoggerTypeEnum.LOGGER};
        }
        return this.types;
    }

    public void setTypes(RequestLoggerTypeEnum[] types) {
        this.types = types;
    }
}

