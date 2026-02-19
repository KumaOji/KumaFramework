/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
 *  org.springframework.context.annotation.Bean
 */
package com.kuma.boot.web.i18n.config;

import com.kuma.boot.web.i18n.DynamicMessageSource;
import com.kuma.boot.web.i18n.I18nMessageProvider;
import com.kuma.boot.web.i18n.MessageSourceHierarchicalChanger;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after={CustomMessageSourceAutoConfiguration.class, MessageSourceAutoConfiguration.class})
public class I18nMessageSourceAutoConfiguration {
    @ConditionalOnBean(value={I18nMessageProvider.class})
    @ConditionalOnMissingBean(name={"messageSource"})
    @Bean(name={"messageSource"})
    public DynamicMessageSource messageSource(I18nMessageProvider i18nMessageProvider) {
        return new DynamicMessageSource(i18nMessageProvider);
    }

    @ConditionalOnBean(name={"messageSource"}, value={I18nMessageProvider.class})
    @Bean(name={"dynamicMessageSource"})
    public DynamicMessageSource dynamicMessageSource(I18nMessageProvider i18nMessageProvider) {
        return new DynamicMessageSource(i18nMessageProvider);
    }

    @ConditionalOnBean(name={"messageSource", "dynamicMessageSource"})
    @Bean
    public MessageSourceHierarchicalChanger messageSourceHierarchicalChanger() {
        return new MessageSourceHierarchicalChanger();
    }
}

