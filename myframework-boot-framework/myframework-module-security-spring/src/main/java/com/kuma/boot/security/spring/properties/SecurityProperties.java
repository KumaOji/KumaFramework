/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.annotation.PostConstruct
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.cloud.context.config.annotation.RefreshScope
 */
package com.kuma.boot.security.spring.properties;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(prefix="kuma.boot.security")
public class SecurityProperties {
    public static final String PREFIX = "kuma.boot.security";
    public static final String[] ENDPOINTS = new String[]{"/actuator/**", "/v3/**", "/*/v3/**", "/fallback", "/favicon.ico", "/swagger-resources/**", "/webjars/**", "/druid/**", "/login/**", "/*/*.html", "/*/*.css", "/*/*.js", "/*.js", "/*.css", "/*.html", "/*/favicon.ico", "/*/api-docs", "/health/**", "/css/**", "/js/**", "/k8s/**", "/k8s", "/images/**", "/doc/**", "/swagger-ui.html", "/startup-report", "/favicon.ico", "/actuator/**", "/index", "/index.html", "/doc.html", "/request/gateway/test", "/error"};
    private List<String> ignoreUrl = new ArrayList<String>();

    @PostConstruct
    public void initIgnoreUrl() {
        Collections.addAll(this.ignoreUrl, ENDPOINTS);
    }

    public List<String> getIgnoreUrl() {
        return this.ignoreUrl;
    }

    public void setIgnoreUrl(List<String> ignoreUrl) {
        this.ignoreUrl = ignoreUrl;
    }
}

