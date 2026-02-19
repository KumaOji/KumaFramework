/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.alibaba.fastjson2.JSON
 *  com.alibaba.fastjson2.JSONObject
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.core.MethodParameter
 *  org.springframework.web.bind.WebDataBinder
 *  org.springframework.web.bind.support.WebDataBinderFactory
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.method.support.HandlerMethodArgumentResolver
 *  org.springframework.web.method.support.ModelAndViewContainer
 */
package com.kuma.boot.web.mvc.resolver;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Objects;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class ActMethodArgumentResolver
implements HandlerMethodArgumentResolver {
    private static final String DEFAULT_VALUE = "body";

    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestJsonParam.class);
    }

    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        RequestJsonParam attribute = (RequestJsonParam)parameter.getParameterAnnotation(RequestJsonParam.class);
        String paramName = attribute.value();
        String value = webRequest.getParameter(StringUtils.isEmpty((String)paramName) ? DEFAULT_VALUE : paramName);
        Class targetParamType = attribute.recordClass();
        Class webParamType = parameter.getParameterType();
        Class paramType = targetParamType != null ? targetParamType : parameter.getParameterType();
        Object result = null;
        if (Objects.equals(paramType, String.class) || Objects.equals(paramType, Integer.class) || Objects.equals(paramType, Long.class) || Objects.equals(paramType, Boolean.class)) {
            JSONObject object = JSON.parseObject((String)value);
            LogUtils.error((String)"ActMethodArgumentResolver resolveArgument,paramName:{}, object:{}", (Object[])new Object[]{paramName, JSON.toJSONString((Object)object)});
            result = object.get(paramName) instanceof Integer && Objects.equals(paramType, Long.class) ? paramType.cast(((Integer)object.get(paramName)).longValue()) : (object.get(paramName) instanceof Integer && Objects.equals(paramType, String.class) ? String.valueOf(object.get(paramName)) : (object.get(paramName) instanceof Long && Objects.equals(paramType, Integer.class) ? paramType.cast(((Long)object.get(paramName)).intValue()) : (object.get(paramName) instanceof Long && Objects.equals(paramType, String.class) ? String.valueOf(object.get(paramName)) : (object.get(paramName) instanceof String && Objects.equals(paramType, Long.class) ? (Number)Long.valueOf((String)object.get(paramName)) : (Number)(object.get(paramName) instanceof String && Objects.equals(paramType, Integer.class) ? Integer.valueOf((String)object.get(paramName)) : paramType.cast(object.get(paramName)))))));
        } else if (paramType.isArray()) {
            if (result != null) {
                Object[] targets = (Object[])result;
                for (int i = 0; i < targets.length; ++i) {
                    WebDataBinder webDataBinder = binderFactory.createBinder(webRequest, targets[i], paramName + "[" + i + "]");
                }
            }
        } else if (Collection.class.isAssignableFrom(paramType)) {
            Class recordClass;
            Class clazz = recordClass = attribute.recordClass() == null ? LinkedHashMap.class : attribute.recordClass();
            if (result != null) {
                Collection targets = (Collection)result;
                int index = 0;
                for (Object targetObj : targets) {
                    WebDataBinder webDataBinder = binderFactory.createBinder(webRequest, targetObj, paramName + "[" + index++ + "]");
                }
            }
        } else {
            result = JSON.parseObject((String)value, (Class)paramType);
        }
        if (result != null) {
            WebDataBinder binder = binderFactory.createBinder(webRequest, result, paramName);
            result = binder.convertIfNecessary(result, paramType, parameter);
            mavContainer.addAttribute(paramName, result);
        }
        return result;
    }
}

