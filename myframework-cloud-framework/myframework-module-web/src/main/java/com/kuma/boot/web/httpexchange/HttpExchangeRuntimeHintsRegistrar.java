/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 *  org.springframework.aot.hint.MemberCategory
 *  org.springframework.aot.hint.ReflectionHints
 *  org.springframework.aot.hint.RuntimeHints
 *  org.springframework.aot.hint.RuntimeHintsRegistrar
 *  org.springframework.web.service.invoker.HttpServiceProxyFactory$Builder
 */
package com.kuma.boot.web.httpexchange;

import org.jspecify.annotations.Nullable;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

class HttpExchangeRuntimeHintsRegistrar
implements RuntimeHintsRegistrar {
    HttpExchangeRuntimeHintsRegistrar() {
    }

    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
        ReflectionHints reflection = hints.reflection();
        reflection.registerType(HttpServiceProxyFactory.Builder.class, new MemberCategory[]{MemberCategory.ACCESS_DECLARED_FIELDS});
    }
}

