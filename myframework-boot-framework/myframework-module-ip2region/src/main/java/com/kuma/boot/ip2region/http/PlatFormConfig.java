/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.kuma.boot.ip2region.http;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlatFormConfig {
    @Bean
    @ConditionalOnProperty(prefix="platform", name={"mode"}, havingValue="baidu")
    public IPlatFormIpAnalyzeService initBaiduIpAnalyzeService() {
        return new PlatformBaiduService();
    }

    @Bean
    @ConditionalOnProperty(prefix="platform", name={"mode"}, havingValue="gaode")
    public IPlatFormIpAnalyzeService initGaodeIpAnalyzeService() {
        return new PlatformGaodeService();
    }
}

