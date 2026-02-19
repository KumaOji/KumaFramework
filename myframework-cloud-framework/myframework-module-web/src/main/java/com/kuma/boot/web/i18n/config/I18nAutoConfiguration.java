/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.MessageSource
 *  org.springframework.context.annotation.Bean
 */
package com.kuma.boot.web.i18n.config;

import com.kuma.boot.web.i18n.I18nResponseAdvice;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after={I18nMessageSourceAutoConfiguration.class})
@EnableConfigurationProperties(value={I18nProperties.class})
public class I18nAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public I18nResponseAdvice i18nResponseAdvice(MessageSource messageSource, I18nProperties i18nProperties) {
        return new I18nResponseAdvice(messageSource, i18nProperties);
    }
}

