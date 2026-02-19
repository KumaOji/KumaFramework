/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.reflect.AnnotationUtils
 *  jakarta.servlet.http.HttpServletResponse
 *  org.jspecify.annotations.NonNull
 *  org.springframework.core.MethodParameter
 *  org.springframework.http.HttpOutputMessage
 *  org.springframework.http.MediaType
 *  org.springframework.http.MediaTypeFactory
 *  org.springframework.http.server.ServletServerHttpResponse
 *  org.springframework.util.Assert
 *  org.springframework.util.MimeType
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler
 *  org.springframework.web.method.support.ModelAndViewContainer
 */
package com.kuma.boot.office.fastexcel.resolver;

import com.kuma.boot.common.utils.reflect.AnnotationUtils;
import com.kuma.boot.office.fastexcel.FastExcelSupport;
import com.kuma.boot.office.fastexcel.annotation.ResponseExcel;
import com.kuma.boot.office.fastexcel.converter.ExcelHttpMessageConverter;
import com.kuma.boot.office.fastexcel.exception.ExcelExportException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

public class ExcelMethodReturnValueHandler
implements AsyncHandlerMethodReturnValueHandler {
    public static final String UTF8 = "UTF-8";

    public boolean supportsReturnType(@NonNull MethodParameter returnType) {
        return AnnotationUtils.hasAnnotationElement((MethodParameter)returnType, ResponseExcel.class);
    }

    public void handleReturnValue(Object returnValue, @NonNull MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws IOException {
        mavContainer.setRequestHandled(true);
        HttpServletResponse response = (HttpServletResponse)webRequest.getNativeResponse(HttpServletResponse.class);
        ResponseExcel responseExcel = (ResponseExcel)AnnotationUtils.getAnnotationElement((MethodParameter)returnType, ResponseExcel.class);
        Assert.notNull((Object)response, (String)"response not be null");
        Assert.notNull((Object)responseExcel, (String)"responseExcel not be null");
        ExcelHttpMessageConverter converter = ExcelHttpMessageConverter.writeExcel(returnType, responseExcel);
        if (!converter.canWrite(returnType.getParameterType(), MediaType.ALL)) {
            throw new ExcelExportException("the return class is not java.util.Collection or java.util.Map");
        }
        String fileName = FastExcelSupport.fileName(responseExcel);
        String contentType = MediaTypeFactory.getMediaType((String)fileName).map(MimeType::toString).orElse("application/vnd.ms-excel");
        response.setCharacterEncoding(UTF8);
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        converter.write(returnValue, MediaType.valueOf((String)contentType), (HttpOutputMessage)new ServletServerHttpResponse(response));
    }

    public boolean isAsyncReturnValue(Object returnValue, @NonNull MethodParameter returnType) {
        return AnnotationUtils.hasAnnotationElement((MethodParameter)returnType, ResponseExcel.class);
    }
}

