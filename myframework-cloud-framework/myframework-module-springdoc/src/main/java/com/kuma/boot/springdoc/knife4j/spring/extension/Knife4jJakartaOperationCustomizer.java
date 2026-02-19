/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.github.xiaoymin.knife4j.annotations.ApiOperationSupport
 *  com.github.xiaoymin.knife4j.annotations.ApiSupport
 *  com.github.xiaoymin.knife4j.core.util.StrUtil
 *  com.github.xiaoymin.knife4j.extend.util.ExtensionUtils
 *  io.swagger.v3.oas.models.Operation
 *  org.springdoc.core.customizers.GlobalOperationCustomizer
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.web.method.HandlerMethod
 */
package com.kuma.boot.springdoc.knife4j.spring.extension;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.github.xiaoymin.knife4j.extend.util.ExtensionUtils;
import io.swagger.v3.oas.models.Operation;
import java.lang.reflect.Method;
import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;

public class Knife4jJakartaOperationCustomizer
implements GlobalOperationCustomizer {
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        ApiOperationSupport operationSupport = (ApiOperationSupport)AnnotationUtils.findAnnotation((Method)handlerMethod.getMethod(), ApiOperationSupport.class);
        if (operationSupport != null) {
            String author = ExtensionUtils.getAuthors((ApiOperationSupport)operationSupport);
            if (StrUtil.isNotBlank((CharSequence)author)) {
                operation.addExtension("x-author", (Object)author);
            }
            if (operationSupport.order() != 0) {
                operation.addExtension("x-order", (Object)operationSupport.order());
            }
        } else {
            ApiSupport apiSupport = (ApiSupport)AnnotationUtils.findAnnotation((Class)handlerMethod.getBeanType(), ApiSupport.class);
            if (apiSupport != null) {
                String author = ExtensionUtils.getAuthor((ApiSupport)apiSupport);
                if (StrUtil.isNotBlank((CharSequence)author)) {
                    operation.addExtension("x-author", (Object)author);
                }
                if (apiSupport.order() != 0) {
                    operation.addExtension("x-order", (Object)apiSupport.order());
                }
            }
        }
        return operation;
    }
}

