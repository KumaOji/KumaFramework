/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication$Type
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.web.reactive.function.client.ClientRequest
 *  org.springframework.web.reactive.function.client.ClientResponse
 *  org.springframework.web.reactive.function.client.ExchangeFilterFunction
 *  org.springframework.web.reactive.function.client.ExchangeFunction
 *  reactor.core.publisher.Mono
 */
package com.kuma.boot.web.support.reactive;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Configuration
@ConditionalOnWebApplication(type=ConditionalOnWebApplication.Type.REACTIVE)
public class HttpHeadersFilterFunction
implements ExchangeFilterFunction {
    private static final String[] ALLOW_HEADS = new String[]{"X-Real-IP", "x-forwarded-for"};

    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return null;
    }
}

