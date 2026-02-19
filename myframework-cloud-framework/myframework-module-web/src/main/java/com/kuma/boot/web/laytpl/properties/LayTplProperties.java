/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.cloud.context.config.annotation.RefreshScope
 */
package com.kuma.boot.web.laytpl.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(prefix="kuma.boot.laytpl")
public class LayTplProperties {
    public static final String PREFIX = "kuma.boot.laytpl";
    private boolean enabled = false;
    private String open = "{{";
    private String close = "}}";
    private String prefix = "classpath:templates/tpl/";
    private boolean cache = true;
    private String numPattern = "#.00";
    private String datePattern = "yyyy-MM-dd HH:mm:ss";
    private String localTimePattern = "HH:mm:ss";
    private String localDatePattern = "yyyy-MM-dd";
    private String localDateTimePattern = "yyyy-MM-dd HH:mm:ss";

    public String getOpen() {
        return this.open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return this.close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isCache() {
        return this.cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public String getNumPattern() {
        return this.numPattern;
    }

    public void setNumPattern(String numPattern) {
        this.numPattern = numPattern;
    }

    public String getDatePattern() {
        return this.datePattern;
    }

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    public String getLocalTimePattern() {
        return this.localTimePattern;
    }

    public void setLocalTimePattern(String localTimePattern) {
        this.localTimePattern = localTimePattern;
    }

    public String getLocalDatePattern() {
        return this.localDatePattern;
    }

    public void setLocalDatePattern(String localDatePattern) {
        this.localDatePattern = localDatePattern;
    }

    public String getLocalDateTimePattern() {
        return this.localDateTimePattern;
    }

    public void setLocalDateTimePattern(String localDateTimePattern) {
        this.localDateTimePattern = localDateTimePattern;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

