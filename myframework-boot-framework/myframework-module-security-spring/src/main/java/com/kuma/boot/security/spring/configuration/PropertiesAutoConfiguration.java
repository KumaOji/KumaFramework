/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 */
package com.kuma.boot.security.spring.configuration;

import com.kuma.boot.security.spring.properties.OAuth2AuthorizationProperties;
import com.kuma.boot.security.spring.properties.OAuth2EndpointProperties;
import com.kuma.boot.security.spring.properties.SecurityProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(value={OAuth2AuthorizationProperties.class, OAuth2EndpointProperties.class, SecurityProperties.class})
public class PropertiesAutoConfiguration {
}

