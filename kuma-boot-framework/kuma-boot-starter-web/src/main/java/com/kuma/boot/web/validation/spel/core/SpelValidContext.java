package com.kuma.boot.web.validation.spel.core;


import java.util.Locale;
import java.util.Objects;

/**
 * Spel校验上下文
 *
 * @author 阿杆
 * @since 2025/4/10
 */
public class SpelValidContext {
    @Override
    public boolean equals(Object o) {

        if (o == null || getClass() != o.getClass()) return false;
        SpelValidContext that = (SpelValidContext) o;
        return Objects.equals(locale, that.locale);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(locale);
    }

    @Override
    public String toString() {
        return "SpelValidContext{" +
                "locale=" + locale +
                '}';
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    Locale locale;

    private static final SpelValidContext DEFAULT = SpelValidContext.builder().build();

    public static SpelValidContext getDefault() {
        return DEFAULT;
    }

    public Locale getLocale() {
        return locale == null ? Locale.getDefault() : locale;
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
            spelValidContext.setLocale(locale);
            return spelValidContext;
        }
    }
}
