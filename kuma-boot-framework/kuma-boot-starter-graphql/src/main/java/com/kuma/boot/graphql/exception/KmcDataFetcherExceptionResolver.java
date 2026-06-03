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

package com.kuma.boot.graphql.exception;

import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.graphql.autoconfigure.properties.GraphqlProperties;
import graphql.ErrorClassification;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.lang.Nullable;

/**
 * Kuma 统一 GraphQL 异常解析器.
 *
 * <p>将业务异常（{@link BaseException} 及子类）和常见运行时异常统一映射为 GraphQL 错误响应，
 * 错误扩展字段结构：
 * <pre>
 * "extensions": {
 *   "code":   "BUSINESS_ERROR",   // 错误分类
 *   "bizCode": "xxx",             // 业务错误码（BaseException 专有）
 *   "stackTrace": "..."           // 仅 exposeStackTrace=true 时出现
 * }
 * </pre>
 *
 * @author kuma
 */
public class KmcDataFetcherExceptionResolver extends DataFetcherExceptionResolverAdapter {

    private final GraphqlProperties.ExceptionHandler config;

    public KmcDataFetcherExceptionResolver(GraphqlProperties properties) {
        this.config = properties.getExceptionHandler();
    }

    @Override
    @Nullable
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof BaseException be) {
            return buildError(ex, ErrorType.DataFetchingException, "BUSINESS_ERROR",
                    Map.of("bizCode", be.getCode() != null ? be.getCode().getCode() : "UNKNOWN"));
        }
        if (ex instanceof IllegalArgumentException) {
            return buildError(ex, ErrorType.ValidationError, "INVALID_ARGUMENT", Map.of());
        }
        // jakarta.validation.ConstraintViolationException — 用类名检测，避免硬依赖
        if (ex.getClass().getName().endsWith("ConstraintViolationException")) {
            return buildError(ex, ErrorType.ValidationError, "CONSTRAINT_VIOLATION", Map.of());
        }
        // 其他未知异常：隐藏内部信息，只记录 INTERNAL_ERROR
        return buildError(ex, ErrorType.DataFetchingException, "INTERNAL_ERROR", Map.of());
    }

    // ── 工具方法 ──────────────────────────────────────────────────────────────

    private GraphQLError buildError(Throwable ex, ErrorClassification classification,
                                    String code, Map<String, Object> extra) {
        Map<String, Object> extensions = new LinkedHashMap<>();
        extensions.put("code", code);
        extensions.putAll(extra);
        if (config.isExposeStackTrace()) {
            extensions.put("stackTrace", stackTrace(ex));
        }
        return GraphqlErrorBuilder.newError()
                .errorType(classification)
                .message(ex.getMessage())
                .extensions(extensions)
                .build();
    }

    private static String stackTrace(Throwable ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
