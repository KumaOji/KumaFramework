/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.core;

import java.util.Locale;
import java.util.Objects;

public class SpelValidContext {
    Locale locale;
    private static final SpelValidContext DEFAULT = SpelValidContext.builder().build();

    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SpelValidContext that = (SpelValidContext)o;
        return Objects.equals(this.locale, that.locale);
    }

    public int hashCode() {
        return Objects.hashCode(this.locale);
    }

    public String toString() {
        return "SpelValidContext{locale=" + String.valueOf(this.locale) + "}";
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public static SpelValidContext getDefault() {
        return DEFAULT;
    }

    public Locale getLocale() {
        return this.locale == null ? Locale.getDefault() : this.locale;
    }

    public static SpelValidContextBuilder builder() {
        return new SpelValidContextBuilder();
    }

    public static final class SpelValidContextBuilder {
        private Locale locale;

        private SpelValidContextBuilder() {
        }

        public SpelValidContextBuilder locale(Locale locale) {
            this.locale = locale;
            return this;
        }

        public SpelValidContext build() {
            SpelValidContext spelValidContext = new SpelValidContext();
            spelValidContext.setLocale(this.locale);
            return spelValidContext;
        }
    }
}

