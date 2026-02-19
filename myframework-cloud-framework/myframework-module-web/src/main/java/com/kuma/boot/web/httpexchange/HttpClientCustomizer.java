/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.web.client.RestClient$Builder
 *  org.springframework.web.reactive.function.client.WebClient$Builder
 */
package com.kuma.boot.web.httpexchange;

import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

public sealed interface HttpClientCustomizer<T> {
    public void customize(T var1, HttpExchangeProperties.Channel var2);

    public static interface WebClientCustomizer
    extends HttpClientCustomizer<WebClient.Builder> {
    }

    public static interface RestClientCustomizer
    extends HttpClientCustomizer<RestClient.Builder> {
    }
}

