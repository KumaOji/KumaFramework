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
@ConfigurationProperties(prefix="kuma.boot.web.filter")
public class WebMvcFilterProperties {
    public static final String PREFIX = "kuma.boot.web.filter";
    private Boolean version = true;
    private Boolean tenant = true;
    private Boolean trace = true;
    private Boolean webContext = true;
    private Boolean report = true;
    private Boolean ping = true;

    public Boolean getVersion() {
        return this.version;
    }

    public void setVersion(Boolean version) {
        this.version = version;
    }

    public Boolean getTenant() {
        return this.tenant;
    }

    public void setTenant(Boolean tenant) {
        this.tenant = tenant;
    }

    public Boolean getTrace() {
        return this.trace;
    }

    public void setTrace(Boolean trace) {
        this.trace = trace;
    }

    public Boolean getWebContext() {
        return this.webContext;
    }

    public void setWebContext(Boolean webContext) {
        this.webContext = webContext;
    }

    public Boolean getReport() {
        return this.report;
    }

    public void setReport(Boolean report) {
        this.report = report;
    }

    public Boolean getPing() {
        return this.ping;
    }

    public void setPing(Boolean ping) {
        this.ping = ping;
    }
}

