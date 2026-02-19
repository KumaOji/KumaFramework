/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  io.swagger.v3.oas.models.Components
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.boot.context.properties.NestedConfigurationProperty
 */
package com.kuma.boot.springdoc.springdocextension.autoconfigure;

import io.swagger.v3.oas.models.Components;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(value="springdoc")
public class SpringDocExtensionProperties {
    @NestedConfigurationProperty
    private Components components;

    public Components getComponents() {
        return this.components;
    }

    public void setComponents(Components components) {
        this.components = components;
    }
}

