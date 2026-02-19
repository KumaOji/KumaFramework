/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.NonNull
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.ResolvableType
 *  org.springframework.http.HttpInputMessage
 *  org.springframework.http.HttpOutputMessage
 *  org.springframework.http.MediaType
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.http.converter.HttpMessageNotReadableException
 *  org.springframework.http.converter.HttpMessageNotWritableException
 */
package com.kuma.boot.office.fastexcel.converter;

import com.kuma.boot.office.fastexcel.ExcelDataType;
import com.kuma.boot.office.fastexcel.FastExcelSupport;
import com.kuma.boot.office.fastexcel.annotation.RequestExcel;
import com.kuma.boot.office.fastexcel.annotation.ResponseExcel;
import com.kuma.boot.office.fastexcel.listener.ExcelMapReadListener;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

public class ExcelHttpMessageConverter
implements HttpMessageConverter<Object> {
    private final ExcelMapReadListener<?> listener;
    private final MethodParameter parameter;
    private final RequestExcel requestExcel;
    private final ResponseExcel responseExcel;

    public ExcelHttpMessageConverter(ExcelMapReadListener<?> listener, MethodParameter parameter, RequestExcel requestExcel, ResponseExcel responseExcel) {
        this.listener = listener;
        this.parameter = parameter;
        this.requestExcel = requestExcel;
        this.responseExcel = responseExcel;
    }

    public static ExcelHttpMessageConverter readExcel(ExcelMapReadListener<?> listener, MethodParameter parameter, RequestExcel requestExcel) {
        return new ExcelHttpMessageConverter(listener, parameter, requestExcel, null);
    }

    public static ExcelHttpMessageConverter writeExcel(MethodParameter parameter, ResponseExcel responseExcel) {
        return new ExcelHttpMessageConverter(null, parameter, null, responseExcel);
    }

    public boolean canRead(@NonNull Class<?> clazz, MediaType mediaType) {
        try {
            ExcelDataType.match(clazz);
        }
        catch (Exception e) {
            return false;
        }
        return mediaType.toString().startsWith("multipart/form-data");
    }

    public boolean canWrite(@NonNull Class<?> type, MediaType mediaType) {
        return Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
    }

    public @NonNull List<MediaType> getSupportedMediaTypes() {
        return List.of();
    }

    public @NonNull Object read(@NonNull Class<?> parameterType, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        ExcelDataType dataType = ExcelDataType.match(parameterType);
        Class<?> excelModelClass = dataType.getFunction().apply(ResolvableType.forMethodParameter((MethodParameter)this.parameter));
        FastExcelSupport.read(inputMessage.getBody(), excelModelClass, this.listener, this.requestExcel.ignoreEmptyRow());
        return this.listener.getData(dataType);
    }

    public void write(@NonNull Object returnValue, MediaType contentType, @NonNull HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (returnValue instanceof Collection) {
            Class excelModelClass = ResolvableType.forMethodParameter((MethodParameter)this.parameter).resolveGeneric(new int[]{0});
            FastExcelSupport.write(outputMessage.getBody(), excelModelClass, this.responseExcel.template(), Map.of("sheet", (Collection)returnValue));
        } else if (returnValue instanceof Map) {
            Map result = (Map)returnValue;
            Class excelModelClass = ResolvableType.forMethodParameter((MethodParameter)this.parameter).getGeneric(new int[]{1}).resolveGeneric(new int[]{0});
            FastExcelSupport.write(outputMessage.getBody(), excelModelClass, this.responseExcel.template(), result);
        }
    }
}

