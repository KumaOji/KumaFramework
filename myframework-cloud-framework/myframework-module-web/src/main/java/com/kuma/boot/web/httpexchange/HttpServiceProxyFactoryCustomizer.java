/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.web.service.invoker.HttpServiceProxyFactory$Builder
 */
package com.kuma.boot.web.httpexchange;

import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@FunctionalInterface
public interface HttpServiceProxyFactoryCustomizer {
    public void customize(HttpServiceProxyFactory.Builder var1);
}

