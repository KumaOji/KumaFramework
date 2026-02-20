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

package com.kuma.boot.web.i18n;

import org.jspecify.annotations.Nullable;
import org.springframework.context.support.AbstractMessageSource;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * 动态获取的 MessageSource，比如从数据库 或者 redis 中获取 message 信息
 */
public class DynamicMessageSource extends AbstractMessageSource {

    public static final String DYNAMIC_MESSAGE_SOURCE_BEAN_NAME = "dynamicMessageSource";

    private final I18nMessageProvider i18nMessageProvider;

    public DynamicMessageSource( I18nMessageProvider i18nMessageProvider) {
        this.i18nMessageProvider = i18nMessageProvider;
    }

    @Override
    @Nullable
    protected MessageFormat resolveCode(String code, Locale locale) {
        I18nMessage i18nMessage = i18nMessageProvider.getI18nMessage(code, locale);
        if (i18nMessage != null) {
            return createMessageFormat(i18nMessage.getMessage(), locale);
        }
        return null;
    }
}
