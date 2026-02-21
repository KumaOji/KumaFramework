/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.cloud.context.config.annotation.RefreshScope
 */
package com.kuma.boot.ip2region.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(value="kuma.boot.ip2region")
public class Ip2regionProperties {
    public static final String PREFIX = "kuma.boot.ip2region";
    private boolean enabled = true;
    private String dbFileLocation = "classpath:ip2region/ip2region.xdb";
    private String ipv6dbFileLocation = "classpath:ip2region/ipv6wry.db";

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getIpv6dbFileLocation() {
        return this.ipv6dbFileLocation;
    }

    public void setIpv6dbFileLocation(String ipv6dbFileLocation) {
        this.ipv6dbFileLocation = ipv6dbFileLocation;
    }

    public String getDbFileLocation() {
        return this.dbFileLocation;
    }

    public void setDbFileLocation(String dbFileLocation) {
        this.dbFileLocation = dbFileLocation;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

