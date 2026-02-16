/*
 * Copyright 2023-2024 the original author or authors.
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

package cn.kuma.blog.framework.web.exception.resolver;

import cn.kuma.blog.common.core.exception.BusinessException;
import cn.kuma.blog.common.core.exception.handler.GlobalExceptionHandler;
import cn.kuma.blog.common.model.result.ApiResult;
import cn.kuma.blog.common.model.result.SystemResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 全局异常处理
 *
 * @author Hccake
 */
@Setter
@Order
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@ConditionalOnWebApplication
public class GlobalHandlerExceptionResolver {

    private final GlobalExceptionHandler globalExceptionHandler;

    /**
     * 隐藏异常的详细信息。
     */
    private Boolean hideExceptionDetails = true;

    /**
     * 设置隐藏后的提示信息。
     */
    private String hiddenMessage = "系统异常，请联系管理员";

    private String npeErrorMessage = "空指针异常!";

    private boolean isHideExceptionDetails() {
        return Boolean.TRUE.equals(this.hideExceptionDetails);
    }

    /**
     * 全局异常捕获
     * @param e the e
     * @return R
     */
    /**
     * 是否为“客户端主动断开/响应已不可用/流式超时”导致的异常（流式接口常见，无需按错误处理）。
     */
    private static boolean isClientDisconnect(Exception e) {
        if (e instanceof AsyncRequestNotUsableException || e instanceof AsyncRequestTimeoutException) {
            return true;
        }
        String msg = e.getMessage();
        if (msg != null && (msg.contains("Connection reset by peer") || msg.contains("Response not usable after response errors"))) {
            return true;
        }
        Throwable cause = e.getCause();
        if (cause != null && cause.getMessage() != null) {
            String cm = cause.getMessage();
            if (cm.contains("Connection reset by peer") || cm.contains("Response not usable after response errors")) {
                return true;
            }
        }
        return false;
    }

    private static String exceptionMessage(Exception e) {
        return e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<String> handleGlobalException(Exception e, HttpServletRequest request,
            HttpServletResponse response) {
        // 响应已提交（如流式接口已发送 audio/flac）时不再写 body，避免 HttpMessageNotWritableException
        if (response.isCommitted()) {
            if (isClientDisconnect(e)) {
                log.warn("请求地址: {}, 流式已结束（客户端断开/超时等）: {}", request.getRequestURI(), exceptionMessage(e));
            } else {
                log.error("请求地址: {}, 响应已提交后发生异常: {}", request.getRequestURI(), exceptionMessage(e), e);
            }
            return null;
        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        if (isClientDisconnect(e)) {
            log.warn("请求地址: {}, 客户端已断开（如刷新/关闭页面、切换歌曲等）: {}", request.getRequestURI(), exceptionMessage(e));
        } else {
            log.error("请求地址: {}, 全局异常信息 ex={}", request.getRequestURI(), exceptionMessage(e), e);
        }
        this.globalExceptionHandler.handle(e);
        // 当为生产环境, 不适合把具体的异常信息展示给用户, 比如数据库异常信息.
        String errorMessage = isHideExceptionDetails() ? this.hiddenMessage : e.getLocalizedMessage();
        return ApiResult.failed(SystemResultCode.SYSTEM_ALERT, errorMessage);
    }

    /**
     * 空指针异常捕获
     * @param e the e
     * @return R
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<String> handleNullPointerException(NullPointerException e, HttpServletRequest request,
            HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        log.error(String.format("请求地址: %s, 空指针异常 ex=%s", request.getRequestURI(), e.getMessage()), e);
        this.globalExceptionHandler.handle(e);
        // 当为生产环境, 不适合把具体的异常信息展示给用户, 比如数据库异常信息.
        String errorMessage = isHideExceptionDetails() ? this.hiddenMessage : this.npeErrorMessage;
        return ApiResult.failed(SystemResultCode.SYSTEM_ALERT, errorMessage);
    }

    /**
     * MethodArgumentTypeMismatchException 参数类型转换异常
     * @param e the e
     * @return R
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResult<String> handleMethodArgumentTypeMismatchException(Exception e, HttpServletRequest request,
            HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        log.error(String.format("请求地址: %s, 请求入参异常 ex=%s", request.getRequestURI(), e.getMessage()), e);
        this.globalExceptionHandler.handle(e);
        return ApiResult.failed(SystemResultCode.SYSTEM_ALERT, e.getLocalizedMessage());
    }

    /**
     * 请求方式有问题 - MediaType 异常 - Method 异常
     * @return R
     */
    @ExceptionHandler({ HttpMediaTypeNotSupportedException.class, HttpRequestMethodNotSupportedException.class })
    public ApiResult<String> requestNotSupportedException(Exception e, HttpServletRequest request,
            HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        log.error(String.format("请求地址: %s, 请求方式异常 ex=%s", request.getRequestURI(), e.getMessage()), e);
        this.globalExceptionHandler.handle(e);
        return ApiResult.failed(SystemResultCode.SYSTEM_ALERT, e.getLocalizedMessage());
    }

    /**
     * IllegalArgumentException 异常捕获，主要用于Assert
     * @param e the e
     * @return R
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<String> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request,
            HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        log.error(String.format("请求地址: %s, 非法数据输入 ex=%s", request.getRequestURI(), e.getMessage()), e);
        this.globalExceptionHandler.handle(e);
        return ApiResult.failed(SystemResultCode.SYSTEM_ALERT, e.getLocalizedMessage());
    }

    /**
     * validation Exception
     * @param e the e
     * @return R
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<String> handleBodyValidException(BindException e, HttpServletRequest request,
            HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        BindingResult bindingResult = e.getBindingResult();
        String errorMsg = bindingResult.getErrorCount() > 0 ? bindingResult.getAllErrors().getFirst().getDefaultMessage()
                : "未获取到错误信息!";

        log.error(String.format("请求地址: %s, 非法数据输入 ex=%s", request.getRequestURI(), errorMsg));

        this.globalExceptionHandler.handle(e);
        return ApiResult.failed(SystemResultCode.SYSTEM_ALERT, errorMsg);
    }

    /**
     * 单体参数校验异常 validation Exception
     * @param e the e
     * @return R
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<String> handleValidationException(ValidationException e, HttpServletRequest request,
            HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        log.error(String.format("请求地址: %s, 参数校验异常 ex=%s", request.getRequestURI(), e.getMessage()));
        this.globalExceptionHandler.handle(e);
        return ApiResult.failed(SystemResultCode.SYSTEM_ALERT, e.getLocalizedMessage());
    }

    /**
     * 自定义业务异常捕获 业务异常响应码推荐使用200 用 result 结构中的code做为业务错误码标识
     * @param e the e
     * @return R
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<String> handleBallCatException(BusinessException e, HttpServletRequest request,
            HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        log.error("请求地址: {}, 业务异常信息 ex={}", request.getRequestURI(), e.getMessage());
        this.globalExceptionHandler.handle(e);
        return ApiResult.failed(e.getCode(), e.getMessage());
    }

}
