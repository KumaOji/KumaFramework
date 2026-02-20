/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.io.DataBufferUtils
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.core.io.buffer.DataBufferFactory
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.codec.multipart.FilePart
 *  org.springframework.util.StringUtils
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 *  reactor.core.scheduler.Schedulers
 */
package com.kuma.boot.web.support.multipart;

import com.kuma.boot.common.utils.io.DataBufferUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Callable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class ResourceFilePart
implements FilePart {
    private final HttpHeaders headers;
    private final Resource resource;

    public ResourceFilePart(Resource resource, HttpHeaders headers, Resource resource1) {
        this.headers = headers;
        this.resource = resource1;
    }

    public String filename() {
        String name = this.headers().getContentDisposition().getName();
        return StringUtils.hasText((String)name) ? name : this.resource.getFilename();
    }

    public Mono<Void> transferTo(Path dest) {
        return this.blockingOperation(() -> Files.copy(this.resource.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING));
    }

    private Mono<Void> blockingOperation(Callable<?> callable) {
        return Mono.create(sink -> {
            try {
                callable.call();
                sink.success();
            }
            catch (Exception ex) {
                sink.error((Throwable)ex);
            }
        }).publishOn(Schedulers.boundedElastic());
    }

    public String name() {
        return this.filename();
    }

    public HttpHeaders headers() {
        return this.headers;
    }

    public Flux<DataBuffer> content() {
        return DataBufferUtils.read((Resource)this.resource, (DataBufferFactory)DataBufferUtils.DEFAULT_FACTORY, (int)8192).publishOn(Schedulers.boundedElastic());
    }
}

