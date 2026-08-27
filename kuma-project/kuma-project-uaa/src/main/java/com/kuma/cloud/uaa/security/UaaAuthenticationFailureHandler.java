package com.kuma.cloud.uaa.security;

import com.kuma.cloud.uaa.support.LoginAttemptService;
import com.kuma.cloud.uaa.support.LoginAuditService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 登录失败统一出口：记录审计、累计失败次数，并带错误码回到登录页。
 *
 * <p>仅密码错误才累计失败次数——验证码或动态码错误由各自的一次性有效期与限流兜底，
 * 计入锁定会让攻击者仅凭错误的验证码就能锁死任意账号。
 *
 * @author kuma
 */
@Slf4j
@RequiredArgsConstructor
public class UaaAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final String loginPage;
    private final LoginAttemptService attemptService;
    private final LoginAuditService auditService;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        String username = request.getParameter("username");
        String errorCode = resolveErrorCode(exception);

        if ("bad_credentials".equals(errorCode)) {
            attemptService.recordFailure(username);
        }
        auditService.recordFailure(username, errorCode, request);
        log.info("登录失败: username={}, reason={}", username, errorCode);

        setDefaultFailureUrl(loginPage + "?error=" + URLEncoder.encode(errorCode, StandardCharsets.UTF_8));
        super.onAuthenticationFailure(request, response, exception);
    }

    private String resolveErrorCode(AuthenticationException exception) {
        if (exception instanceof UaaLoginException loginException) {
            return loginException.getErrorCode();
        }
        if (exception instanceof DisabledException) {
            return "disabled";
        }
        if (exception instanceof LockedException) {
            return "locked";
        }
        return "bad_credentials";
    }
}
