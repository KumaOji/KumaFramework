package com.kuma.cloud.uaa.security;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

import java.io.Serial;

/**
 * 密码校验之外的登录前置校验失败（验证码、锁定、二次校验）。
 *
 * <p>继承 {@link AuthenticationException} 是为了让前置过滤器能复用与密码校验完全一致的
 * {@code AuthenticationFailureHandler} 处理链路。
 *
 * @author kuma
 */
@Getter
public class UaaLoginException extends AuthenticationException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 回显到 {@code /login?error=} 的错误码。
     */
    private final String errorCode;

    public UaaLoginException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
