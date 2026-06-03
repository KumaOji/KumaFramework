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

package com.kuma.boot.i18n.util;

import java.util.Locale;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.NonNull;

/**
 * 国际化工具类，提供编程式消息获取能力.
 *
 * <p>自动装配为 Spring Bean，通过静态方法在任意位置获取当前 Locale 的翻译文本。
 *
 * <pre>{@code
 * // 使用当前请求 Locale
 * String msg = I18nUtils.get("user.not.found");
 *
 * // 带位置参数
 * String msg = I18nUtils.get("user.not.found", userId);
 *
 * // 指定 Locale
 * String msg = I18nUtils.get("user.not.found", Locale.US);
 * }</pre>
 *
 * @author kuma
 */
public class I18nUtils implements ApplicationContextAware {

    private static MessageSource messageSource;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext ctx) {
        messageSource = ctx.getBean(MessageSource.class);
    }

    /** 获取当前 Locale 的翻译，未找到时返回 code 本身. */
    public static String get(String code) {
        return get(code, null, LocaleContextHolder.getLocale());
    }

    /** 带位置参数，使用当前 Locale. */
    public static String get(String code, Object... args) {
        return get(code, args, LocaleContextHolder.getLocale());
    }

    /** 指定 Locale，无位置参数. */
    public static String get(String code, Locale locale) {
        return get(code, null, locale);
    }

    /** 完整参数，指定 Locale 和位置参数. */
    public static String get(String code, Object[] args, Locale locale) {
        if (messageSource == null) {
            return code;
        }
        try {
            return messageSource.getMessage(code, args, locale);
        } catch (NoSuchMessageException e) {
            return code;
        }
    }

    /** 获取当前请求的 Locale. */
    public static Locale currentLocale() {
        return LocaleContextHolder.getLocale();
    }
}
