/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.xkcoding.http.config.HttpConfig
 */
package com.kuma.boot.security.spring.authentication.login.social.justauth.properties;

import com.xkcoding.http.config.HttpConfig;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;

public class HttpConfigProperties {
    private Boolean enable = false;
    private Proxy.Type proxy = Proxy.Type.HTTP;
    private String hostname;
    private Integer port;
    private Duration timeout = Duration.ofSeconds(3L);
    private Duration foreignTimeout = Duration.ofSeconds(15L);

    public HttpConfig getHttpConfig() {
        if (!this.enable.booleanValue()) {
            return HttpConfig.builder().timeout((int)this.timeout.toMillis()).build();
        }
        return HttpConfig.builder().proxy(new Proxy(this.proxy, new InetSocketAddress(this.hostname, (int)this.port))).timeout((int)this.timeout.toMillis()).build();
    }

    public Boolean getEnable() {
        return this.enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Proxy.Type getProxy() {
        return this.proxy;
    }

    public void setProxy(Proxy.Type proxy) {
        this.proxy = proxy;
    }

    public String getHostname() {
        return this.hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPort() {
        return this.port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Duration getTimeout() {
        return this.timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public Duration getForeignTimeout() {
        return this.foreignTimeout;
    }

    public void setForeignTimeout(Duration foreignTimeout) {
        this.foreignTimeout = foreignTimeout;
    }
}

