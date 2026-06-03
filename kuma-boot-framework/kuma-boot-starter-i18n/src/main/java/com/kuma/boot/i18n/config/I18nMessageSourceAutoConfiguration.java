/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.i18n.config;

import com.kuma.boot.i18n.message.WildcardReloadableResourceBundleMessageSource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * {@link MessageSource} 自动配置.
 *
 * <p>注册 {@link WildcardReloadableResourceBundleMessageSource} 支持多 jar classpath 扫描，
 * 并自动包含本模块内置消息文件 {@code i18n/kuma-i18n-messages}，
 * 业务应用在 {@code spring.messages.basename} 中追加自己的消息包即可。
 *
 * <p>此配置优先于 Spring Boot 的 {@link MessageSourceAutoConfiguration}，
 * 若容器中已存在 {@code messageSource} bean（如 web 模块），则跳过。
 *
 * @author kuma
 */
@AutoConfiguration(before = MessageSourceAutoConfiguration.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnMissingBean(
        name = AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME,
        search = SearchStrategy.CURRENT)
@EnableConfigurationProperties
public class I18nMessageSourceAutoConfiguration {

    /** 内置消息文件路径，始终包含在 basenames 中. */
    private static final String BUILTIN_BASENAME = "i18n/kuma-i18n-messages";

    @Bean
    @ConfigurationProperties(prefix = "spring.messages")
    @ConditionalOnMissingBean
    public MessageSourceProperties messageSourceProperties() {
        return new MessageSourceProperties();
    }

    @Bean(name = AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME)
    public MessageSource messageSource(MessageSourceProperties properties) {
        WildcardReloadableResourceBundleMessageSource ms =
                new WildcardReloadableResourceBundleMessageSource();

        List<String> basenames = new ArrayList<>();
        // 用户配置的 basenames
        if (!CollectionUtils.isEmpty(properties.getBasename())) {
            String joined = properties.getBasename().stream()
                    .map(String::trim)
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");
            basenames.addAll(List.of(StringUtils.commaDelimitedListToStringArray(joined)));
        }
        // 追加内置消息文件
        if (!basenames.contains(BUILTIN_BASENAME)) {
            basenames.add(BUILTIN_BASENAME);
        }
        ms.setBasenames(basenames.toArray(String[]::new));

        if (properties.getEncoding() != null) {
            ms.setDefaultEncoding(properties.getEncoding().name());
        }
        ms.setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
        ms.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage());
        Duration cacheDuration = properties.getCacheDuration();
        if (cacheDuration != null) {
            ms.setCacheMillis(cacheDuration.toMillis());
        }
        ms.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());
        return ms;
    }
}
