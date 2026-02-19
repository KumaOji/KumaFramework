/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.github.xiaoymin.knife4j.core.model.MarkdownProperty
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.springdoc.knife4j.spring.configuration;

import com.github.xiaoymin.knife4j.core.model.MarkdownProperty;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="knife4j")
public class Knife4jProperties {
    private boolean enable = false;
    private boolean cors = false;
    private Knife4jHttpBasic basic;
    private boolean production = false;
    private Knife4jSetting setting;
    private List<MarkdownProperty> documents;

    public boolean isEnable() {
        return this.enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isCors() {
        return this.cors;
    }

    public void setCors(boolean cors) {
        this.cors = cors;
    }

    public Knife4jHttpBasic getBasic() {
        return this.basic;
    }

    public void setBasic(Knife4jHttpBasic basic) {
        this.basic = basic;
    }

    public boolean isProduction() {
        return this.production;
    }

    public void setProduction(boolean production) {
        this.production = production;
    }

    public Knife4jSetting getSetting() {
        return this.setting;
    }

    public void setSetting(Knife4jSetting setting) {
        this.setting = setting;
    }

    public List<MarkdownProperty> getDocuments() {
        return this.documents;
    }

    public void setDocuments(List<MarkdownProperty> documents) {
        this.documents = documents;
    }
}

