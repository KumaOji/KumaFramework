/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.bean.BeanUtils
 *  jakarta.servlet.http.HttpServletRequest
 *  org.jspecify.annotations.NonNull
 *  org.springframework.core.MethodParameter
 *  org.springframework.http.HttpInputMessage
 *  org.springframework.http.MediaType
 *  org.springframework.web.bind.support.WebDataBinderFactory
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.method.support.HandlerMethodArgumentResolver
 *  org.springframework.web.method.support.ModelAndViewContainer
 *  org.springframework.web.multipart.support.RequestPartServletServerHttpRequest
 */
package com.kuma.boot.office.fastexcel.resolver;

import com.kuma.boot.common.utils.bean.BeanUtils;
import com.kuma.boot.office.fastexcel.annotation.ExcelParam;
import com.kuma.boot.office.fastexcel.annotation.RequestExcel;
import com.kuma.boot.office.fastexcel.converter.ExcelHttpMessageConverter;
import com.kuma.boot.office.fastexcel.listener.ExcelMapReadListener;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.support.RequestPartServletServerHttpRequest;

public class ExcelMethodArgumentResolver
implements HandlerMethodArgumentResolver {
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasMethodAnnotation(RequestExcel.class) && parameter.hasParameterAnnotation(ExcelParam.class);
    }

    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        ExcelMapReadListener listener;
        ExcelHttpMessageConverter converter;
        HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest(HttpServletRequest.class);
        RequestExcel requestExcel = (RequestExcel)parameter.getMethodAnnotation(RequestExcel.class);
        ExcelParam excelParam = (ExcelParam)parameter.getParameterAnnotation(ExcelParam.class);
        if (Objects.nonNull(requestExcel) && Objects.nonNull(request) && Objects.nonNull(excelParam) && (converter = ExcelHttpMessageConverter.readExcel(listener = (ExcelMapReadListener)BeanUtils.instantiateClass(requestExcel.parse()), parameter, requestExcel)).canRead(parameter.getParameterType(), MediaType.valueOf((String)request.getContentType()))) {
            return converter.read(parameter.getParameterType(), (HttpInputMessage)new RequestPartServletServerHttpRequest(request, excelParam.fileName()));
        }
        throw new IllegalArgumentException("Excel upload request resolver error, @ExcelData parameter type error");
    }
}

