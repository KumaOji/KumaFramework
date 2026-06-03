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

package com.kuma.boot.i18n.message;

import java.util.Locale;

/**
 * 动态国际化消息提供者 SPI.
 *
 * <p>业务应用实现此接口并注册为 Spring Bean，即可从数据库、Redis 等动态数据源读取翻译，
 * 框架会自动将 {@link DynamicMessageSource} 挂入 {@code MessageSource} 链。
 *
 * <pre>{@code
 * @Component
 * public class DbI18nMessageProvider implements I18nMessageProvider {
 *     public I18nMessage getI18nMessage(String code, Locale locale) {
 *         return i18nMessageRepository.findByCodeAndLocale(code, locale.toLanguageTag());
 *     }
 * }
 * }</pre>
 *
 * @author kuma
 */
public interface I18nMessageProvider {

    /**
     * 根据 code 和 locale 获取国际化消息.
     *
     * @param code   消息唯一标识
     * @param locale 目标语言
     * @return 对应 {@link I18nMessage}，未找到时返回 {@code null}
     */
    I18nMessage getI18nMessage(String code, Locale locale);
}
