/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.BeanWrapperImpl
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.Ordered
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.http.HttpMethod
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.web.bind.annotation.BindParam
 *  org.springframework.web.bind.annotation.RequestParam
 *  org.springframework.web.multipart.MultipartFile
 *  org.springframework.web.service.invoker.HttpRequestValues$Builder
 *  org.springframework.web.service.invoker.HttpServiceArgumentResolver
 *  org.springframework.web.util.UriBuilderFactory
 */
package com.kuma.boot.web.httpexchange;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.BindParam;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.service.invoker.HttpRequestValues;
import org.springframework.web.service.invoker.HttpServiceArgumentResolver;
import org.springframework.web.util.UriBuilderFactory;

public class BeanParamArgumentResolver
implements HttpServiceArgumentResolver,
Ordered {
    private static final Logger log = LoggerFactory.getLogger(BeanParamArgumentResolver.class);
    private static final boolean springQueryMapPresent = ClassUtils.isPresent((String)"org.springframework.cloud.openfeign.SpringQueryMap", (ClassLoader)BeanParamArgumentResolver.class.getClassLoader());
    public static final int ORDER = 0;
    private static final String WEB_BIND_ANNOTATION_PACKAGE = RequestParam.class.getPackageName();
    private final HttpExchangeProperties properties;

    public BeanParamArgumentResolver(HttpExchangeProperties properties) {
        this.properties = properties;
    }

    public boolean resolve(@Nullable Object argument, MethodParameter parameter, HttpRequestValues.Builder requestValues) {
        if (argument == null || BeanParamArgumentResolver.isNonResolvableArgument(argument, parameter)) {
            return false;
        }
        if (BeanParamArgumentResolver.hasAnnotation(parameter, BeanParam.class)) {
            return this.process(argument, requestValues);
        }
        return this.properties.isBeanToQueryEnabled() && this.process(argument, requestValues);
    }

    private boolean process(Object argument, HttpRequestValues.Builder requestValues) {
        if (argument instanceof Map) {
            return false;
        }
        Map<String, Object> nameToValue = this.getPropertyValueMap(argument);
        BeanParamArgumentResolver.populateRequestValuesFromMap(requestValues, nameToValue);
        return true;
    }

    public int getOrder() {
        return 0;
    }

    private static boolean isNonResolvableArgument(Object argument, MethodParameter parameter) {
        return argument instanceof URI || argument instanceof HttpMethod || argument instanceof UriBuilderFactory || argument instanceof MultipartFile || BeanUtils.isSimpleValueType(argument.getClass()) || BeanParamArgumentResolver.hasWebBindPackageAnnotation(parameter);
    }

    private static boolean hasAnnotation(MethodParameter parameter, Class<? extends Annotation> annotationClass) {
        for (Annotation anno : parameter.getParameterAnnotations()) {
            if (anno.annotationType() != annotationClass) continue;
            return true;
        }
        return false;
    }

    private Map<String, Object> getPropertyValueMap(Object source) {
        Class<?> clazz = source.getClass();
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        try {
            PropertyDescriptor[] pds;
            BeanWrapperImpl src = new BeanWrapperImpl(source);
            for (PropertyDescriptor pd : pds = src.getPropertyDescriptors()) {
                String name = pd.getName();
                Object value = src.getPropertyValue(name);
                if (Objects.equals(name, "class") || value == null) continue;
                Field field = ReflectionUtils.findField(clazz, (String)name);
                if (field != null) {
                    BindParam anno = (BindParam)AnnotationUtils.findAnnotation((AnnotatedElement)field, BindParam.class);
                    String mappedName = anno != null && StringUtils.hasText((String)anno.value()) ? anno.value() : name;
                    result.put(mappedName, value);
                    continue;
                }
                result.put(name, value);
            }
        }
        catch (Exception e) {
            log.warn("Failed to convert object[{}] to request parameters", clazz, (Object)e);
        }
        return result;
    }

    private static void populateRequestValuesFromMap(HttpRequestValues.Builder requestValues, Map<String, Object> nameToValue) {
        nameToValue.forEach((k, v) -> {
            Class<?> clz = v.getClass();
            if (BeanUtils.isSimpleValueType(clz)) {
                requestValues.addRequestParameter(k, new String[]{v.toString()});
            } else if (clz.isArray() && BeanUtils.isSimpleValueType(clz.getComponentType())) {
                String[] arrValue = (String[])Arrays.stream((Object[])v).filter(Objects::nonNull).map(Object::toString).toArray(String[]::new);
                if (arrValue.length > 0) {
                    requestValues.addRequestParameter(k, arrValue);
                }
            } else if (v instanceof Iterable) {
                Iterable iter = (Iterable)v;
                ArrayList values = new ArrayList();
                iter.forEach(item -> {
                    if (item != null && BeanUtils.isSimpleValueType(item.getClass())) {
                        values.add(item.toString());
                    }
                });
                if (!values.isEmpty()) {
                    requestValues.addRequestParameter(k, (String[])values.toArray(String[]::new));
                }
            }
        });
    }

    private static boolean hasWebBindPackageAnnotation(MethodParameter parameter) {
        for (Annotation annotation : parameter.getParameterAnnotations()) {
            if (!annotation.annotationType().getPackageName().startsWith(WEB_BIND_ANNOTATION_PACKAGE)) continue;
            return true;
        }
        return false;
    }
}

