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

package com.kuma.boot.web.gracefulresponse.advice;

import tools.jackson.databind.ObjectMapper;
import com.kuma.boot.web.gracefulresponse.ExceptionAliasRegister;
import com.kuma.boot.web.gracefulresponse.GracefulResponseException;
import com.kuma.boot.web.gracefulresponse.GracefulResponseProperties;
import com.kuma.boot.web.gracefulresponse.api.ExceptionAliasFor;
import com.kuma.boot.web.gracefulresponse.api.ExceptionMapper;
import com.kuma.boot.web.gracefulresponse.api.ResponseFactory;
import com.kuma.boot.web.gracefulresponse.api.ResponseStatusFactory;
import com.kuma.boot.web.gracefulresponse.data.Response;
import com.kuma.boot.web.gracefulresponse.data.ResponseStatus;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理.
 */
@ControllerAdvice
@Order(200)
public class GlobalExceptionAdvice implements ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionAdvice.class);

    @Resource private ResponseStatusFactory responseStatusFactory;

    @Resource private ResponseFactory responseFactory;

    @Resource private ObjectMapper objectMapper;

    private ExceptionAliasRegister exceptionAliasRegister;

    @Resource private GracefulResponseProperties gracefulResponseProperties;

    @Resource private GracefulResponseProperties properties;

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    /**
     * 权限不足异常处理（@PreAuthorize 方法级鉴权失败）.
     *
     * <p>直接写入 HttpServletResponse，绕过 Spring 内容协商，避免 SSE 端点（produces=text/event-stream）
     * 无法序列化 JSON 响应体导致前端收不到数据的问题。
     */
    @ExceptionHandler({AccessDeniedException.class})
    public void accessDeniedHandler(AccessDeniedException ex, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        if (gracefulResponseProperties.isPrintExceptionInGlobalAdvice()) {
            logger.debug("Graceful Response:GlobalExceptionAdvice捕获到权限不足异常,message=[{}]",
                    ex.getMessage());
        }
        if (response == null || response.isCommitted()) return;
        Response errorResponse = responseFactory.newInstance(
                responseStatusFactory.newInstance("403", "权限不足"));
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    /**
     * 异常处理逻辑.
     *
     * @param throwable 业务逻辑抛出的异常
     * @param request   当前请求，用于判断是否排除路径
     * @return 统一返回包装后的结果
     */
    @ExceptionHandler({Throwable.class})
    @ResponseBody
    public Response exceptionHandler(Throwable throwable, HttpServletRequest request,
            HttpServletResponse response) {
        // 响应已提交（如流式输出中客户端断开），无法再写入 JSON，直接返回 null 避免 HttpMessageNotWritableException
        if (response != null && response.isCommitted()) {
            if (gracefulResponseProperties.isPrintExceptionInGlobalAdvice()) {
                logger.debug("响应已提交，跳过异常包装: {}", throwable.getMessage());
            }
            return null;
        }
        // 排除路径（如 /v3/api-docs、/swagger-ui）不包装，直接抛出，避免 Swagger UI 收到 GracefulResponse 错误格式
        if (request != null && !CollectionUtils.isEmpty(gracefulResponseProperties.getExcludePaths())) {
            String path = request.getRequestURI();
            if (gracefulResponseProperties.getExcludePaths().stream()
                    .anyMatch(pattern -> ANT_PATH_MATCHER.match(pattern, path))) {
                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }
                throw new RuntimeException(throwable);
            }
        }
        if (gracefulResponseProperties.isPrintExceptionInGlobalAdvice()) {
            // 预期内的业务异常（如未登录）使用 DEBUG，避免日志噪音
            if (isExpectedBusinessException(throwable)) {
                logger.debug("Graceful Response:GlobalExceptionAdvice捕获到预期业务异常,message=[{}]",
                        throwable.getMessage());
            } else {
                logger.error(
                        "Graceful Response:GlobalExceptionAdvice捕获到异常,message=[{}]",
                        throwable.getMessage(),
                        throwable);
            }
        }
        ResponseStatus statusLine;
        if (throwable instanceof GracefulResponseException) {
            statusLine =
                    fromGracefulResponseExceptionInstance((GracefulResponseException) throwable);
        } else {
            // 校验异常转自定义异常
            statusLine = fromExceptionInstance(throwable);
        }
        return responseFactory.newInstance(statusLine);
    }

    private ResponseStatus fromGracefulResponseExceptionInstance(
            GracefulResponseException exception) {
        String code = exception.getCode();
        if (code == null) {
            code = properties.getDefaultErrorCode();
        }
        return responseStatusFactory.newInstance(code, exception.getMsg());
    }

    private ResponseStatus fromExceptionInstance(Throwable throwable) {

        Class<? extends Throwable> clazz = throwable.getClass();

        ExceptionMapper exceptionMapper = clazz.getAnnotation(ExceptionMapper.class);

        // 1.有@ExceptionMapper注解，直接设置结果的状态
        if (exceptionMapper != null) {
            boolean msgReplaceable = exceptionMapper.msgReplaceable();
            // 异常提示可替换+抛出来的异常有自定义的异常信息
            if (msgReplaceable) {
                String throwableMessage = throwable.getMessage();
                if (throwableMessage != null) {
                    return responseStatusFactory.newInstance(
                            exceptionMapper.code(), throwableMessage);
                }
            }
            return responseStatusFactory.newInstance(exceptionMapper.code(), exceptionMapper.msg());
        }

        // 2.有@ExceptionAliasFor异常别名注解，获取已注册的别名信息
        if (exceptionAliasRegister != null) {
            ExceptionAliasFor exceptionAliasFor =
                    exceptionAliasRegister.getExceptionAliasFor(clazz);
            if (exceptionAliasFor != null) {
                return responseStatusFactory.newInstance(
                        exceptionAliasFor.code(), exceptionAliasFor.msg());
            }
        }
        ResponseStatus defaultError = responseStatusFactory.defaultError();

        // 3. 原生异常+originExceptionUsingDetailMessage=true
        // 如果有自定义的异常信息，原生异常将直接使用异常信息进行返回，不再返回默认错误提示
        if (properties.getOriginExceptionUsingDetailMessage()) {
            String throwableMessage = throwable.getMessage();
            if (throwableMessage != null) {
                defaultError.setMsg(throwableMessage);
            }
        }
        return defaultError;
    }

    /**
     * 是否为预期内的业务异常（如未登录、用户名密码错误），仅记录 DEBUG 避免日志噪音
     */
    private boolean isExpectedBusinessException(Throwable throwable) {
        if (throwable == null) return false;
        String className = throwable.getClass().getName();
        if (!className.contains("BusinessException")) return false;
        String msg = throwable.getMessage();
        if (msg == null) return false;
        return msg.contains("未登录") || msg.contains("用户名或密码错误");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.exceptionAliasRegister = applicationContext.getBean(ExceptionAliasRegister.class);
    }
}
