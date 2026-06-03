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

import java.text.MessageFormat;
import java.util.Locale;
import org.jspecify.annotations.Nullable;
import org.springframework.context.support.AbstractMessageSource;

/**
 * 动态 {@code MessageSource}，从 {@link I18nMessageProvider} 实时获取翻译.
 *
 * <p>当业务应用提供了 {@link I18nMessageProvider} Bean（从 DB / Redis 读取），框架自动将
 * 本 Source 挂入 {@code MessageSource} 层级链：
 * <pre>
 *   properties MessageSource
 *       └── DynamicMessageSource （父级，先查文件再查动态）
 * </pre>
 *
 * @author kuma
 */
public class DynamicMessageSource extends AbstractMessageSource {

    public static final String DYNAMIC_MESSAGE_SOURCE_BEAN_NAME = "dynamicMessageSource";

    private final I18nMessageProvider provider;

    public DynamicMessageSource(I18nMessageProvider provider) {
        this.provider = provider;
    }

    @Override
    @Nullable
    protected MessageFormat resolveCode(String code, Locale locale) {
        I18nMessage msg = provider.getI18nMessage(code, locale);
        return msg != null ? createMessageFormat(msg.getMessage(), locale) : null;
    }
}
