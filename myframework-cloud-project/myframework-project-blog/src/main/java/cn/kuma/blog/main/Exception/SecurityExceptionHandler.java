package cn.kuma.blog.main.Exception;

import cn.kuma.blog.common.model.result.ApiResult;
import cn.kuma.blog.common.model.result.SystemResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 处理 Spring Security 方法级权限异常（@PreAuthorize 失败）。
 * 方法安全抛出的 AccessDeniedException/AuthorizationDeniedException 不会经过 Security 的
 * AccessDeniedHandler，会被 ControllerAdvice 捕获；此处统一返回 403 + ApiResult JSON。
 *
 * @author Kuma
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
@org.springframework.core.annotation.Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityExceptionHandler {

    @ExceptionHandler({ AccessDeniedException.class, AuthorizationDeniedException.class })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResult<String> handleAccessDenied(Exception e) {
        log.warn("权限不足: {}", e.getMessage());
        return ApiResult.failed(SystemResultCode.FORBIDDEN, "权限不足");
    }
}
