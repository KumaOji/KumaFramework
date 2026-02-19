/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.cloud.context.config.annotation.RefreshScope
 */
package com.kuma.boot.web.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(prefix="kuma.boot.web.interceptor")
public class WebMvcInterceptorProperties {
    public static final String PREFIX = "kuma.boot.web.interceptor";
    private Boolean doubtApi = true;
    private int doubtApiThreshold = 0x300000;
    private Boolean header = true;

    public Boolean getDoubtApi() {
        return this.doubtApi;
    }

    public void setDoubtApi(Boolean doubtApi) {
        this.doubtApi = doubtApi;
    }

    public Boolean getHeader() {
        return this.header;
    }

    public void setHeader(Boolean header) {
        this.header = header;
    }

    public int getDoubtApiThreshold() {
        return this.doubtApiThreshold;
    }

    public void setDoubtApiThreshold(int doubtApiThreshold) {
        this.doubtApiThreshold = doubtApiThreshold;
    }
}

