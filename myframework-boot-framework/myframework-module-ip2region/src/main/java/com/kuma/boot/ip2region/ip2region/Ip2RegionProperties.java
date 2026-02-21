/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.io.ResourceUtils
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.core.io.Resource
 */
package com.kuma.boot.ip2region.ip2region;

import com.kuma.boot.common.utils.io.ResourceUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties(value="ip2region")
public class Ip2RegionProperties {
    public static final String PREFIX = "ip2region";
    private Boolean enabled = false;
    private String filePath = "classpath:ip/ip2region.xdb";

    public Resource getFileResource() {
        return ResourceUtils.getResource((String)this.filePath);
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}

