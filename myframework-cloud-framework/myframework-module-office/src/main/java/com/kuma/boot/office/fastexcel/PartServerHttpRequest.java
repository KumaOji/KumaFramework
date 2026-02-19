/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.NonNull
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.codec.multipart.Part
 *  org.springframework.http.server.reactive.ServerHttpRequest
 *  org.springframework.http.server.reactive.ServerHttpRequestDecorator
 *  reactor.core.publisher.Flux
 */
package com.kuma.boot.office.fastexcel;

import org.jspecify.annotations.NonNull;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;

public class PartServerHttpRequest
extends ServerHttpRequestDecorator {
    private final Part part;

    public PartServerHttpRequest(ServerHttpRequest delegate, Part part) {
        super(delegate);
        this.part = part;
    }

    public @NonNull HttpHeaders getHeaders() {
        return this.part.headers();
    }

    public @NonNull Flux<DataBuffer> getBody() {
        return this.part.content();
    }
}

