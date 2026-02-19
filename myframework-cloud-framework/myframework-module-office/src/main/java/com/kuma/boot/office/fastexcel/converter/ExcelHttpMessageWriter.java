/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.io.DataBufferUtils
 *  org.reactivestreams.Publisher
 *  org.springframework.core.ResolvableType
 *  org.springframework.http.MediaType
 *  org.springframework.http.ReactiveHttpOutputMessage
 *  org.springframework.http.codec.HttpMessageWriter
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package com.kuma.boot.office.fastexcel.converter;

import com.kuma.boot.common.utils.io.DataBufferUtils;
import com.kuma.boot.office.fastexcel.FastExcelSupport;
import com.kuma.boot.office.fastexcel.annotation.ResponseExcel;
import com.kuma.boot.office.fastexcel.exception.ExcelExportException;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.HttpMessageWriter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ExcelHttpMessageWriter
implements HttpMessageWriter<Object> {
    private static final Function<Collection<?>, Map<String, Collection<?>>> defaultFunction = c -> Map.of("sheet", c);

    public List<MediaType> getWritableMediaTypes() {
        return List.of();
    }

    public boolean canWrite(ResolvableType elementType, MediaType mediaType) {
        if (Flux.class.isAssignableFrom(elementType.toClass())) {
            return true;
        }
        if (Mono.class.isAssignableFrom(elementType.toClass())) {
            Class type = elementType.resolveGeneric(new int[]{0});
            return this.canWrite(type);
        }
        return this.canWrite(elementType.toClass());
    }

    private boolean canWrite(Class<?> type) {
        return Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
    }

    public Mono<Void> write(Publisher<?> inputStream, ResolvableType elementType, MediaType mediaType, ReactiveHttpOutputMessage message, Map<String, Object> hints) {
        ResponseExcel responseExcel = (ResponseExcel)hints.get("responseExcel");
        if (inputStream instanceof Flux) {
            Flux flux = (Flux)inputStream;
            Class excelModelClass = elementType.resolveGeneric(new int[]{0});
            Mono mono = flux.collectList().map(defaultFunction);
            return this.write(responseExcel, message, excelModelClass, mono);
        }
        if (inputStream instanceof Mono) {
            ResolvableType type;
            ResolvableType resolvableType = type = Mono.class.isAssignableFrom(elementType.toClass()) ? elementType.getGeneric(new int[]{0}) : elementType;
            if (Collection.class.isAssignableFrom(type.toClass())) {
                Class excelModelClass = type.resolveGeneric(new int[]{0});
                Mono mono = ((Mono)inputStream).map(defaultFunction);
                return this.write(responseExcel, message, excelModelClass, mono);
            }
            if (Map.class.isAssignableFrom(type.toClass())) {
                Class excelModelClass = type.resolveGeneric(new int[]{1});
                Mono mono = (Mono)inputStream;
                return this.write(responseExcel, message, excelModelClass, mono);
            }
        }
        throw new ExcelExportException("the return class is not java.util.Collection or java.util.Map");
    }

    private Mono<Void> write(ResponseExcel excelReturn, ReactiveHttpOutputMessage message, Class<?> excelModelClass, Mono<Map<String, Collection<?>>> result) {
        return result.flatMap(r -> this.write(excelReturn, message, excelModelClass, (Map<String, Collection<?>>)r));
    }

    private Mono<Void> write(ResponseExcel excelReturn, ReactiveHttpOutputMessage message, Class<?> excelModelClass, Map<String, Collection<?>> result) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        FastExcelSupport.write(outputStream, excelModelClass, excelReturn.template(), result);
        Flux bufferFlux = DataBufferUtils.transform((byte[])outputStream.toByteArray());
        return message.writeWith((Publisher)bufferFlux);
    }
}

