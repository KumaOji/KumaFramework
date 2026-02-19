/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.validation.MessageInterpolator$Context
 *  org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator
 *  org.hibernate.validator.spi.resourceloading.ResourceBundleLocator
 */
package com.kuma.boot.web.i18n;

import jakarta.validation.MessageInterpolator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Locale;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

public class EmptyCurlyToDefaultMessageInterpolator
extends ResourceBundleMessageInterpolator {
    private static final String EMPTY_CURLY_BRACES = "{}";

    public EmptyCurlyToDefaultMessageInterpolator() {
    }

    public EmptyCurlyToDefaultMessageInterpolator(ResourceBundleLocator userResourceBundleLocator) {
        super(userResourceBundleLocator);
    }

    public String interpolate(String message, MessageInterpolator.Context context, Locale locale) {
        if (message.contains(EMPTY_CURLY_BRACES)) {
            Object defaultValue;
            Method messageMethod;
            Class<? extends Annotation> annotationType = context.getConstraintDescriptor().getAnnotation().annotationType();
            try {
                messageMethod = annotationType.getDeclaredMethod("message", new Class[0]);
            }
            catch (NoSuchMethodException e) {
                return super.interpolate(message, context, locale);
            }
            if (messageMethod.getDefaultValue() != null && (defaultValue = messageMethod.getDefaultValue()) instanceof String) {
                String defaultMessage = (String)defaultValue;
                message = message.replace(EMPTY_CURLY_BRACES, defaultMessage);
            }
        }
        return super.interpolate(message, context, locale);
    }
}

