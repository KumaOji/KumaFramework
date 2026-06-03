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

package com.kuma.boot.webflux.autoconfigure;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.exception.InnerException;
import com.kuma.boot.common.exception.LockException;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.NestedCheckedException;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * WebFlux 全局异常处理自动配置
 *
 * <p>拦截控制器层抛出的异常并统一转换为 {@link Result} 响应体，仅在 REACTIVE Web 环境下激活。
 *
 * @author kuma
 */
@AutoConfiguration
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class WebFluxExceptionAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(WebFluxExceptionAutoConfiguration.class, StarterNameConstants.WEBFLUX_STARTER);
    }

    // ── 业务异常 ────────────────────────────────────────────────────────────────

    @ExceptionHandler(BaseException.class)
    public Mono<Result<String>> handleBaseException(BaseException e, ServerWebExchange exchange) {
        LogUtils.error(e);
        return Mono.just(Result.fail(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public Mono<Result<String>> handleBusinessException(BusinessException e, ServerWebExchange exchange) {
        LogUtils.error(e);
        return Mono.just(Result.fail(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(InnerException.class)
    public Mono<Result<String>> handleInnerException(InnerException e) {
        LogUtils.error(e);
        return Mono.just(Result.fail(ResultEnum.INNER_ERROR));
    }

    @ExceptionHandler(LockException.class)
    public Mono<Result<String>> handleLockException(LockException e) {
        LogUtils.error(e);
        return Mono.just(Result.fail(ResultEnum.FAILED));
    }

    // ── 参数校验异常 ─────────────────────────────────────────────────────────────

    /** WebFlux 中对应 MVC 的 MethodArgumentNotValidException */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<Result<String>> handleWebExchangeBindException(WebExchangeBindException e) {
        LogUtils.error(e);
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String msg = fieldErrors.isEmpty() ? "请求参数错误" : fieldErrors.get(0).getDefaultMessage();
        return Mono.just(Result.fail(ResultEnum.BAD_REQUEST.code(), msg));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<Result<String>> handleIllegalArgumentException(IllegalArgumentException e) {
        LogUtils.error(e);
        return Mono.just(Result.fail(ResultEnum.ILLEGAL_ARGUMENT_ERROR));
    }

    // ── HTTP 层异常 ──────────────────────────────────────────────────────────────

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<Result<String>> handleResponseStatusException(ResponseStatusException e) {
        LogUtils.error(e);
        HttpStatus status = HttpStatus.resolve(e.getStatusCode().value());
        if (status == HttpStatus.NOT_FOUND) {
            return Mono.just(Result.fail(ResultEnum.REQUEST_NOT_FOUND));
        }
        if (status == HttpStatus.METHOD_NOT_ALLOWED) {
            return Mono.just(Result.fail(ResultEnum.METHOD_NOT_SUPPORTED_ERROR));
        }
        if (status == HttpStatus.UNSUPPORTED_MEDIA_TYPE) {
            return Mono.just(Result.fail(ResultEnum.MEDIA_TYPE_NOT_SUPPORTED_ERROR));
        }
        return Mono.just(Result.fail(e.getReason() != null ? e.getReason() : e.getMessage()));
    }

    // ── Spring 嵌套异常 ──────────────────────────────────────────────────────────

    @ExceptionHandler(NestedRuntimeException.class)
    public Mono<Result<String>> handleNestedRuntimeException(NestedRuntimeException e) {
        LogUtils.error(e);
        return Mono.just(Result.fail(e.getMessage()));
    }

    @ExceptionHandler(NestedCheckedException.class)
    public Mono<Result<String>> handleNestedCheckedException(NestedCheckedException e) {
        LogUtils.error(e);
        return Mono.just(Result.fail(e.getMessage()));
    }

    // ── 兜底异常 ─────────────────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<Result<String>> handleException(Exception e, ServerWebExchange exchange) {
        LogUtils.error(e);
        return Mono.just(Result.fail());
    }

    @ExceptionHandler(Error.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<Result<String>> handleError(Error e) {
        LogUtils.error(new Exception(e.getMessage(), e));
        return Mono.just(Result.fail());
    }
}
