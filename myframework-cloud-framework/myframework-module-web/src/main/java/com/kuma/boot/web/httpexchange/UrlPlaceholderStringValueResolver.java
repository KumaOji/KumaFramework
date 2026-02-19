/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.env.Environment
 *  org.springframework.util.StringValueResolver
 */
package com.kuma.boot.web.httpexchange;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.StringValueResolver;

public class UrlPlaceholderStringValueResolver
implements StringValueResolver {
    private static final Logger log = LoggerFactory.getLogger(UrlPlaceholderStringValueResolver.class);
    private final Environment environment;
    private final @Nullable StringValueResolver delegate;

    public UrlPlaceholderStringValueResolver(Environment environment, @Nullable StringValueResolver delegate) {
        this.environment = environment;
        this.delegate = delegate;
    }

    public @Nullable String resolveStringValue(String strVal) {
        String resolved = strVal;
        try {
            resolved = this.environment.resolvePlaceholders(strVal);
        }
        catch (Exception e) {
            log.warn("Placeholders in '{}' could not be resolved", (Object)strVal, (Object)e);
        }
        return this.delegate != null ? this.delegate.resolveStringValue(resolved) : resolved;
    }
}

