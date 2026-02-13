/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.ByteArrayResource
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.core.io.buffer.DataBufferFactory
 *  org.springframework.core.io.buffer.DataBufferUtils
 *  org.springframework.core.io.buffer.DefaultDataBufferFactory
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 *  reactor.core.scheduler.Schedulers
 */
package com.kuma.boot.common.utils.io;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class DataBufferUtils
extends org.springframework.core.io.buffer.DataBufferUtils {
    public static final int BUFFER_SIZE = 8192;
    public static final DataBufferFactory DEFAULT_FACTORY = DefaultDataBufferFactory.sharedInstance;

    public static Mono<InputStream> transform(Flux<DataBuffer> dataBufferFlux) {
        return DataBufferUtils.join(dataBufferFlux).map(DataBuffer::asInputStream);
    }

    public static Flux<DataBuffer> transform(byte[] array) {
        ByteArrayResource resource = new ByteArrayResource(array);
        return DataBufferUtils.read((Resource)resource, (DataBufferFactory)DEFAULT_FACTORY, (int)8192);
    }

    public static Mono<byte[]> transformByte(Flux<DataBuffer> bufferFlux) {
        return DataBufferUtils.transform(bufferFlux).publishOn(Schedulers.boundedElastic()).handle((inputStream, sink) -> {
            try {
                sink.next((Object)inputStream.readAllBytes());
            }
            catch (IOException e) {
                sink.error((Throwable)new RuntimeException(e));
            }
        });
    }

    public static Flux<DataBuffer> transform(InputStream inputStream) {
        return DataBufferUtils.readInputStream(() -> inputStream, (DataBufferFactory)DEFAULT_FACTORY, (int)8192);
    }

    public static Flux<DataBuffer> transform(Mono<InputStream> inputStreamMono) {
        return inputStreamMono.flatMapMany(DataBufferUtils::transform);
    }
}

