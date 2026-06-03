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

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * kuma i18n 增强配置（{@code kuma.boot.i18n.*}）.
 *
 * <p>Spring 原生的消息源配置仍通过 {@code spring.messages.*} 设置；
 * 此处仅维护 kuma 侧的扩展配置。
 *
 * @author kuma
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = I18nProperties.PREFIX)
public class I18nProperties {

    public static final String PREFIX = "kuma.boot.i18n";

    /** 是否启用 i18n 增强（响应翻译 / 异常处理 / 工具类）. */
    private boolean enabled = true;

    /**
     * 回退语言标签（BCP 47，如 {@code zh-CN}）.
     *
     * <p>当找不到请求 Locale 对应的消息时，尝试回退到此语言；
     * 设为空则不回退。
     */
    private String fallbackLanguageTag = "zh-CN";

    /**
     * 找不到消息时是否将 code 作为默认消息返回，而非抛出 {@code NoSuchMessageException}.
     */
    private boolean useCodeAsDefaultMessage = true;

    /**
     * Locale 解析策略，默认读取 HTTP {@code Accept-Language} 头.
     */
    private LocaleResolverType localeResolverType = LocaleResolverType.ACCEPT_HEADER;

    /**
     * 支持的语言列表（BCP 47），空则不限制.
     *
     * <p>限制后，客户端请求非列表内语言时将回退到 {@link #defaultLanguage}。
     */
    private List<String> supportedLanguages = new ArrayList<>(List.of("zh-CN", "en-US"));

    /**
     * 默认语言（BCP 47），当 Locale 无法解析时使用.
     */
    private String defaultLanguage = "zh-CN";

    /**
     * Cookie 名称，{@link LocaleResolverType#COOKIE} 模式下有效.
     */
    private String cookieName = "lang";

    /**
     * Cookie 最大有效期（秒），{@code -1} 表示会话级（关闭浏览器即失效）.
     * {@link LocaleResolverType#COOKIE} 模式下有效。
     */
    private int cookieMaxAge = -1;

    /**
     * Locale 解析策略枚举.
     */
    public enum LocaleResolverType {
        /** 读取 HTTP {@code Accept-Language} 请求头（默认）. */
        ACCEPT_HEADER,
        /** 从 Cookie 读取（需配合 {@code LocaleChangeInterceptor} 写入）. */
        COOKIE,
        /** 从 HTTP Session 读取（需配合 {@code LocaleChangeInterceptor} 写入）. */
        SESSION,
        /** 固定语言，始终使用 {@link #defaultLanguage}（用于测试）. */
        FIXED
    }
}
