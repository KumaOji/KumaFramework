/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.lang.Assert
 *  cn.hutool.core.text.CharSequenceUtil
 *  cn.hutool.core.util.StrUtil
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.servlet.mvc.condition.RequestCondition
 *  org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
 */
package com.kuma.boot.web.support.version;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.web.annotation.ApiVersion;

import java.lang.reflect.Method;
import java.util.Objects;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class ApiVersionRequestMappingHandlerMapping
extends RequestMappingHandlerMapping {
    private ApiVersionProperties apiVersionProperties;

    public ApiVersionRequestMappingHandlerMapping(ApiVersionProperties apiVersionProperties) {
        this.apiVersionProperties = apiVersionProperties;
    }

    public ApiVersionProperties getApiVersionProperties() {
        return this.apiVersionProperties;
    }

    public void setApiVersionProperties(ApiVersionProperties apiVersionProperties) {
        this.apiVersionProperties = apiVersionProperties;
    }

    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        ApiVersion apiVersion = (ApiVersion)AnnotationUtils.findAnnotation(handlerType, ApiVersion.class);
        return this.createRequestCondition(apiVersion, handlerType);
    }

    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        ApiVersion apiVersion = (ApiVersion)AnnotationUtils.findAnnotation((Method)method, ApiVersion.class);
        return this.createRequestCondition(apiVersion, method.getDeclaringClass());
    }

    private RequestCondition<ApiVersionRequestCondition> createRequestCondition(ApiVersion apiVersion, Class<?> handlerType) {
        if (Objects.isNull(apiVersion)) {
            return null;
        }
        RequestMapping requestMapping = (RequestMapping)AnnotationUtils.findAnnotation(handlerType, RequestMapping.class);
        if (requestMapping == null) {
            return null;
        }
        CharSequence[] requestMappingValues = requestMapping.value();
        if (StrUtil.isAllEmpty((CharSequence[])requestMappingValues) || !((String)requestMappingValues[0]).contains(this.apiVersionProperties.getVersionPlaceholder())) {
            return null;
        }
        String[] versionPlaceholderValues = CharSequenceUtil.splitToArray((CharSequence)requestMappingValues[0], (CharSequence)"/");
        Integer index = null;
        for (int i = 0; i < versionPlaceholderValues.length; ++i) {
            if (!StrUtil.equals((CharSequence)versionPlaceholderValues[i], (CharSequence)this.apiVersionProperties.getVersionPlaceholder())) continue;
            index = i;
            break;
        }
        if (index == null) {
            return null;
        }
        double value = apiVersion.value();
        Assert.isTrue((value >= 1.0 ? 1 : 0) != 0, (String)"Api Version Must be greater than or equal to 1", (Object[])new Object[0]);
        return new ApiVersionRequestCondition(apiVersion, this.apiVersionProperties, index);
    }
}

