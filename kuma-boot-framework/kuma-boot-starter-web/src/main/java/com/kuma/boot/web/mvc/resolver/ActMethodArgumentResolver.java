/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.web.mvc.resolver;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Collection;
import java.util.Objects;

public class ActMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String DEFAULT_VALUE = "body";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        /** 只有指定注解注释的参数才会走当前自定义参数解析器 */
        return parameter.hasParameterAnnotation(RequestJsonParam.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory)
            throws Exception {
        /** 获取参数注解 */
        RequestJsonParam attribute = parameter.getParameterAnnotation(
                RequestJsonParam.class);

        /** 获取参数名 */
        String paramName = attribute.value();
        /** 获取指定名字参数的值 */
        String value =
                webRequest.getParameter(StringUtils.isEmpty(paramName) ? DEFAULT_VALUE : paramName);
        /** 获取注解设定参数类型 */
        Class<?> targetParamType = attribute.recordClass();
        /** 获取实际参数类型 */
        Class<?> webParamType = parameter.getParameterType();
        /** 以自定义参数类型为准 */
        Class<?> paramType =
                targetParamType != null ? targetParamType : parameter.getParameterType();
        Object result = null;
        if (Objects.equals(paramType, String.class)
                || Objects.equals(paramType, Integer.class)
                || Objects.equals(paramType, Long.class)
                || Objects.equals(paramType, Boolean.class)) {
            JSONObject object = JSON.parseObject(value);
            LogUtils.error(
                    "ActMethodArgumentResolver resolveArgument,paramName:{}, object:{}",
                    paramName,
                    JSON.toJSONString(object));
            if (object.get(paramName) instanceof Integer && Objects.equals(paramType, Long.class)) {
                // 入参：Integer  目标类型：Long
                result = paramType.cast(((Integer) object.get(paramName)).longValue());
            } else if (object.get(paramName) instanceof Integer
                    && Objects.equals(paramType, String.class)) {
                // 入参：Integer  目标类型：String
                result = String.valueOf(object.get(paramName));
            } else if (object.get(paramName) instanceof Long
                    && Objects.equals(paramType, Integer.class)) {
                // 入参：Long  目标类型：Integer（精度丢失）
                result = paramType.cast(((Long) object.get(paramName)).intValue());
            } else if (object.get(paramName) instanceof Long
                    && Objects.equals(paramType, String.class)) {
                // 入参：Long  目标类型：String
                result = String.valueOf(object.get(paramName));
            } else if (object.get(paramName) instanceof String
                    && Objects.equals(paramType, Long.class)) {
                // 入参：String  目标类型：Long
                result = Long.valueOf((String) object.get(paramName));
            } else if (object.get(paramName) instanceof String
                    && Objects.equals(paramType, Integer.class)) {
                // 入参：String  目标类型：Integer
                result = Integer.valueOf((String) object.get(paramName));
            } else {
                result = paramType.cast(object.get(paramName));
            }
        } else if (paramType.isArray()) {
            // TODO: result = JsonHelper.fromJson(value, paramType);
        } else if (Collection.class.isAssignableFrom(paramType)) {
            // TODO: result = JsonHelper.fromJsonArrayBy(value, attribute.recordClass(), paramType);
        } else {
            result = JSON.parseObject(value, paramType);
        }

        if (result != null) {
            /** 参数绑定 */
            WebDataBinder binder = binderFactory.createBinder(webRequest, result, paramName);
            result = binder.convertIfNecessary(result, paramType, parameter);
            // validateIfApplicable(binder, parameter, annotations);
            mavContainer.addAttribute(paramName, result);
        }
        return result;
    }
}
