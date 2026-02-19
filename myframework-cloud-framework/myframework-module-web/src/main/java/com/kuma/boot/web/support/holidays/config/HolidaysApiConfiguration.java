/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.core.io.ResourceLoader
 */
package com.kuma.boot.web.support.holidays.config;

import com.kuma.boot.web.support.holidays.core.HolidaysApi;
import com.kuma.boot.web.support.holidays.impl.HolidaysApiImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

@AutoConfiguration
@EnableConfigurationProperties(value={HolidaysApiProperties.class})
public class HolidaysApiConfiguration {
    @Bean
    public HolidaysApi holidaysApi(ResourceLoader resourceLoader, HolidaysApiProperties properties) {
        return new HolidaysApiImpl(resourceLoader, properties);
    }
}

