/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 */
package com.kuma.boot.web.support.version;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value="kuma.boot.web.api-version")
public class ApiVersionProperties {
    private boolean enabled = true;
    private double minimumVersion;
    private String versionPlaceholder = "{version}";

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public double getMinimumVersion() {
        return this.minimumVersion;
    }

    public void setMinimumVersion(double minimumVersion) {
        this.minimumVersion = minimumVersion;
    }

    public String getVersionPlaceholder() {
        return this.versionPlaceholder;
    }

    public void setVersionPlaceholder(String versionPlaceholder) {
        this.versionPlaceholder = versionPlaceholder;
    }
}

