/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.context.annotation.Configuration
 */
package com.kuma.boot.web.exception.properties;

import com.kuma.boot.web.exception.enums.ExceptionHandleTypeEnum;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="kuma.boot.web.global-exception")
public class ExceptionHandleProperties {
    public static final String PREFIX = "kuma.boot.web.global-exception";
    private ExceptionHandleTypeEnum[] types = new ExceptionHandleTypeEnum[]{ExceptionHandleTypeEnum.LOGGER};
    private Boolean enabled = true;
    private Boolean ignoreChild = true;
    private Set<Class<? extends Throwable>> ignoreExceptions = new HashSet<Class<? extends Throwable>>();
    private long time = TimeUnit.MINUTES.toSeconds(5L);
    private int max = 5;
    private int length = 3000;
    private Set<String> receiveEmails = new HashSet<String>(0);

    public ExceptionHandleTypeEnum[] getTypes() {
        return this.types;
    }

    public void setTypes(ExceptionHandleTypeEnum[] types) {
        this.types = types;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getIgnoreChild() {
        return this.ignoreChild;
    }

    public void setIgnoreChild(Boolean ignoreChild) {
        this.ignoreChild = ignoreChild;
    }

    public Set<Class<? extends Throwable>> getIgnoreExceptions() {
        return this.ignoreExceptions;
    }

    public void setIgnoreExceptions(Set<Class<? extends Throwable>> ignoreExceptions) {
        this.ignoreExceptions = ignoreExceptions;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getMax() {
        return this.max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Set<String> getReceiveEmails() {
        return this.receiveEmails;
    }

    public void setReceiveEmails(Set<String> receiveEmails) {
        this.receiveEmails = receiveEmails;
    }
}

