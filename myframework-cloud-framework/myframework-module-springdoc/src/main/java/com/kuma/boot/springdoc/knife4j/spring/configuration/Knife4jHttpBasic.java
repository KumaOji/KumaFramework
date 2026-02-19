/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 */
package com.kuma.boot.springdoc.knife4j.spring.configuration;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="knife4j.basic")
public class Knife4jHttpBasic {
    private boolean enable = false;
    private String username;
    private String password;
    private List<String> include;

    public boolean isEnable() {
        return this.enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getInclude() {
        return this.include;
    }

    public void setInclude(List<String> include) {
        this.include = include;
    }
}

