/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 *  org.springframework.context.support.AbstractMessageSource
 */
package com.kuma.boot.web.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import org.jspecify.annotations.Nullable;
import org.springframework.context.support.AbstractMessageSource;

public class DynamicMessageSource
extends AbstractMessageSource {
    public static final String DYNAMIC_MESSAGE_SOURCE_BEAN_NAME = "dynamicMessageSource";
    private final I18nMessageProvider i18nMessageProvider;

    public DynamicMessageSource(I18nMessageProvider i18nMessageProvider) {
        this.i18nMessageProvider = i18nMessageProvider;
    }

    protected @Nullable MessageFormat resolveCode(String code, Locale locale) {
        I18nMessage i18nMessage = this.i18nMessageProvider.getI18nMessage(code, locale);
        if (i18nMessage != null) {
            return this.createMessageFormat(i18nMessage.getMessage(), locale);
        }
        return null;
    }
}

