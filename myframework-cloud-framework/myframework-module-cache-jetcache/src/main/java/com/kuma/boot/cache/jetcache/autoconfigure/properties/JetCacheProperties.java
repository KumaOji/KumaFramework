//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.cache.jetcache.autoconfigure.properties;

import com.google.common.base.MoreObjects;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties("kuma.boot.cache.jetcache")
public class JetCacheProperties {
    public static final String PREFIX = "kuma.boot.cache.jetcache";
    private boolean enabled = true;
    private Boolean desensitization = true;
    private Boolean clearRemoteOnExit = false;
    private Boolean allowNullValues = true;
    private String separator = "-";
    private Map<String, Expire> expires = new HashMap();

    public Boolean getDesensitization() {
        return this.desensitization;
    }

    public void setDesensitization(Boolean desensitization) {
        this.desensitization = desensitization;
    }

    public Boolean getClearRemoteOnExit() {
        return this.clearRemoteOnExit;
    }

    public void setClearRemoteOnExit(Boolean clearRemoteOnExit) {
        this.clearRemoteOnExit = clearRemoteOnExit;
    }

    public Boolean getAllowNullValues() {
        return this.allowNullValues;
    }

    public void setAllowNullValues(Boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }

    public Map<String, Expire> getExpires() {
        return this.expires;
    }

    public void setExpires(Map<String, Expire> expires) {
        this.expires = expires;
    }

    public String getSeparator() {
        return this.separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("desensitization", this.desensitization).add("clearRemoteOnExit", this.clearRemoteOnExit).add("allowNullValues", this.allowNullValues).add("separator", this.separator).toString();
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
