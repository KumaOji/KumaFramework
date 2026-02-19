/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.context.support.ResourceBundleMessageSource
 */
package com.kuma.boot.web.validation.spel.core.message;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Locale;
import org.springframework.context.support.ResourceBundleMessageSource;

public class ResourceBundleMessageResolver {
    public static final String DEFAULT_VALIDATION_MESSAGES = "ValidationMessages";
    private static final ResourceBundleMessageSource MESSAGE_SOURCE = ResourceBundleMessageResolver.initMessageSource();

    private ResourceBundleMessageResolver() {
    }

    private static ResourceBundleMessageSource initMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(new String[]{DEFAULT_VALIDATION_MESSAGES});
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    public static void resetBasenames() {
        MESSAGE_SOURCE.setBasenames(new String[]{DEFAULT_VALIDATION_MESSAGES});
    }

    public static void addBasenames(String ... basename) {
        String[] existingBasename = MESSAGE_SOURCE.getBasenameSet().toArray(new String[0]);
        String[] combinedBasename = new String[basename.length + existingBasename.length];
        System.arraycopy(basename, 0, combinedBasename, 0, basename.length);
        System.arraycopy(existingBasename, 0, combinedBasename, basename.length, existingBasename.length);
        LogUtils.debug((String)"Combined basename: {}", (Object[])new Object[]{combinedBasename});
        MESSAGE_SOURCE.setBasenames(combinedBasename);
    }

    public static String getMessage(String key, Locale locale, Object ... args) {
        return MESSAGE_SOURCE.getMessage(key, args, locale);
    }
}

