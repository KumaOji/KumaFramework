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

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.i18n.advice.I18nResponseAdvice;
import com.kuma.boot.i18n.exception.I18nExceptionHandler;
import com.kuma.boot.i18n.util.I18nUtils;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * i18n 主自动配置.
 *
 * <p>激活条件：{@code kuma.boot.i18n.enabled=true}（默认）。按需注册：
 * <ul>
 *   <li>{@link I18nUtils} —— 编程式消息获取工具（始终注册）</li>
 *   <li>{@link LocaleResolver} —— Locale 解析策略（Web 环境）</li>
 *   <li>{@link I18nResponseAdvice} —— 响应体字段多语言替换（Web 环境）</li>
 *   <li>{@link I18nExceptionHandler} —— {@code I18nException} 全局处理（Web 环境）</li>
 * </ul>
 *
 * @author kuma
 */
@AutoConfiguration(after = {
        I18nMessageSourceAutoConfiguration.class,
        I18nDynamicMessageSourceConfiguration.class
})
@EnableConfigurationProperties(I18nProperties.class)
@ConditionalOnProperty(
        prefix = I18nProperties.PREFIX,
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class I18nAutoConfiguration implements InitializingBean {

    @Bean
    @ConditionalOnMissingBean
    public I18nUtils i18nUtils() {
        return new I18nUtils();
    }

    // ── Web-only beans ────────────────────────────────────────────────────

    @Bean
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnMissingBean(LocaleResolver.class)
    public LocaleResolver localeResolver(I18nProperties props) {
        List<Locale> supported = props.getSupportedLanguages().stream()
                .map(Locale::forLanguageTag)
                .toList();
        Locale defaultLocale = Locale.forLanguageTag(props.getDefaultLanguage());

        return switch (props.getLocaleResolverType()) {
            case COOKIE -> {
                CookieLocaleResolver r = new CookieLocaleResolver(props.getCookieName());
                r.setDefaultLocale(defaultLocale);
                r.setCookieMaxAge(Duration.ofSeconds(props.getCookieMaxAge()));
                if (!supported.isEmpty()) r.setSupportedLocales(supported);
                yield r;
            }
            case SESSION -> {
                SessionLocaleResolver r = new SessionLocaleResolver();
                r.setDefaultLocale(defaultLocale);
                if (!supported.isEmpty()) r.setSupportedLocales(supported);
                yield r;
            }
            case FIXED -> new FixedLocaleResolver(defaultLocale);
            default -> {  // ACCEPT_HEADER
                AcceptHeaderLocaleResolver r = new AcceptHeaderLocaleResolver();
                r.setDefaultLocale(defaultLocale);
                if (!supported.isEmpty()) r.setSupportedLocales(supported);
                yield r;
            }
        };
    }

    @Bean
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnMissingBean
    public I18nResponseAdvice i18nResponseAdvice(MessageSource messageSource, I18nProperties props) {
        return new I18nResponseAdvice(messageSource, props);
    }

    @Bean
    @ConditionalOnWebApplication(type = Type.SERVLET)
    @ConditionalOnMissingBean
    public I18nExceptionHandler i18nExceptionHandler(MessageSource messageSource) {
        return new I18nExceptionHandler(messageSource);
    }

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(I18nAutoConfiguration.class, StarterNameConstants.I18N_STARTER);
    }
}
