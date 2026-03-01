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

package com.kuma.boot.web.support.version;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.web.annotation.ApiVersion;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Objects;

import static cn.hutool.core.text.CharSequenceUtil.splitToArray;

/**
 * RESTful API接口版本控制
 *
 * @author kuma
 * @version 2022.06
 * @since 2022-07-27 20:20:44
 */
public class ApiVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private com.kuma.boot.web.support.version.ApiVersionProperties apiVersionProperties;

    public ApiVersionRequestMappingHandlerMapping(
            com.kuma.boot.web.support.version.ApiVersionProperties apiVersionProperties) {
        this.apiVersionProperties = apiVersionProperties;
    }

    public com.kuma.boot.web.support.version.ApiVersionProperties getApiVersionProperties() {
        return apiVersionProperties;
    }

    public void setApiVersionProperties( com.kuma.boot.web.support.version.ApiVersionProperties apiVersionProperties) {
        this.apiVersionProperties = apiVersionProperties;
    }

    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        // 扫描类或接口上的 {@link ApiVersion}
        ApiVersion apiVersion = AnnotationUtils.findAnnotation(handlerType, ApiVersion.class);
        return createRequestCondition(apiVersion, handlerType);
    }

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        // 扫描方法上的 {@link ApiVersion}
        ApiVersion apiVersion = AnnotationUtils.findAnnotation(method, ApiVersion.class);
        return createRequestCondition(apiVersion, method.getDeclaringClass());
    }

    private RequestCondition<com.kuma.boot.web.support.version.ApiVersionRequestCondition> createRequestCondition(
            ApiVersion apiVersion, Class<?> handlerType) {
        // 1. 确认是否进行版本控制-ApiVersion注解不为空
        if (Objects.isNull(apiVersion)) {
            return null;
        }

        // 2. 确认是否进行版本控制-RequestMapping注解包含版本占位符
        RequestMapping requestMapping =
                AnnotationUtils.findAnnotation(handlerType, RequestMapping.class);
        if (requestMapping == null) {
            return null;
        }

        String[] requestMappingValues = requestMapping.value();
        if (StrUtil.isAllEmpty(requestMappingValues)
                || !requestMappingValues[0].contains(
                apiVersionProperties.getVersionPlaceholder())) {
            return null;
        }

        // 3. 解析版本占位符索引位置
        String[] versionPlaceholderValues = splitToArray(requestMappingValues[0], "/");
        Integer index = null;
        for (int i = 0; i < versionPlaceholderValues.length; i++) {
            if (StrUtil.equals(
                    versionPlaceholderValues[i], apiVersionProperties.getVersionPlaceholder())) {
                index = i;
                break;
            }
        }

        // 4. 确认是否进行版本控制-占位符索引确认
        if (index == null) {
            return null;
        }

        // 5. 确认是否满足最低版本（v1）要求
        double value = apiVersion.value();
        Assert.isTrue(value >= 1, "Api Version Must be greater than or equal to 1");

        // 6. 创建 RequestCondition
        return new com.kuma.boot.web.support.version.ApiVersionRequestCondition(apiVersion, apiVersionProperties, index);
    }
}
