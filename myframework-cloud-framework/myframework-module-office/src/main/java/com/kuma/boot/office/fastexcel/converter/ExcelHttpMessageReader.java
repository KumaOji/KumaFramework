/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.io.DataBufferUtils
 *  org.jspecify.annotations.NonNull
 *  org.springframework.core.ResolvableType
 *  org.springframework.http.MediaType
 *  org.springframework.http.ReactiveHttpInputMessage
 *  org.springframework.http.codec.HttpMessageReader
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package com.kuma.boot.office.fastexcel.converter;

import com.kuma.boot.common.utils.io.DataBufferUtils;
import com.kuma.boot.office.fastexcel.ExcelDataType;
import com.kuma.boot.office.fastexcel.FastExcelSupport;
import com.kuma.boot.office.fastexcel.annotation.RequestExcel;
import com.kuma.boot.office.fastexcel.listener.ExcelMapReadListener;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.HttpMessageReader;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ExcelHttpMessageReader
implements HttpMessageReader<Object> {
    public @NonNull List<MediaType> getReadableMediaTypes() {
        return List.of(MediaType.MULTIPART_FORM_DATA);
    }

    public boolean canRead(@NonNull ResolvableType elementType, MediaType mediaType) {
        try {
            ResolvableType type = elementType;
            if (Objects.equals(type.resolve(), Mono.class)) {
                type = elementType.getGeneric(new int[]{0});
            }
            if (type.getRawClass() == null) {
                return false;
            }
            if (Flux.class.isAssignableFrom(type.getRawClass())) {
                return true;
            }
            ExcelDataType.match(type.getRawClass());
        }
        catch (Exception e) {
            return false;
        }
        return mediaType.toString().startsWith("multipart/form-data");
    }

    public @NonNull Flux<Object> read(@NonNull ResolvableType elementType, @NonNull ReactiveHttpInputMessage message, @NonNull Map<String, Object> hints) {
        throw new UnsupportedOperationException("");
    }

    public @NonNull Mono<Object> readMono(@NonNull ResolvableType elementType, @NonNull ReactiveHttpInputMessage message, @NonNull Map<String, Object> hints) {
        ExcelMapReadListener listener = (ExcelMapReadListener)hints.get("listener");
        RequestExcel requestExcel = (RequestExcel)hints.get("requestExcel");
        if (Objects.equals(elementType.resolve(), Mono.class)) {
            elementType = elementType.getGeneric(new int[]{0});
        }
        if (elementType.getRawClass() != null) {
            ExcelDataType dataType = Flux.class.isAssignableFrom(elementType.getRawClass()) ? ExcelDataType.COLLECTION : ExcelDataType.match(elementType.getRawClass());
            Class<?> excelModelClass = dataType.getFunction().apply(elementType);
            return Mono.just((Object)message.getBody()).flatMap(DataBufferUtils::transform).doOnSuccess(in -> FastExcelSupport.read(in, excelModelClass, listener, requestExcel.ignoreEmptyRow())).map(in -> listener.getData(dataType));
        }
        return Mono.empty();
    }
}

