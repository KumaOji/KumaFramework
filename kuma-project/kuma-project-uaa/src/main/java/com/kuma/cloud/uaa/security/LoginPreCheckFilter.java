package com.kuma.cloud.uaa.security;

import com.kuma.cloud.uaa.domain.entity.UaaUser;
import com.kuma.cloud.uaa.service.MfaService;
import com.kuma.cloud.uaa.service.UaaUserService;
import com.kuma.cloud.uaa.support.LoginAttemptService;
import com.kuma.cloud.uaa.support.LoginCaptchaService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 登录表单前置校验：图形验证码 → 账号锁定 → TOTP 动态码，全部通过后才把请求交给
 * {@code UsernamePasswordAuthenticationFilter} 做密码校验。
 *
 * <p>刻意不声明为 {@code @Component}：Filter 类型的 Bean 会被 Spring Boot 额外注册到
 * Servlet 容器上对所有请求生效，这里只应作用于安全过滤器链中的登录端点。
 *
 * @author kuma
 */
@RequiredArgsConstructor
public class LoginPreCheckFilter extends OncePerRequestFilter {

    public static final String PARAM_CAPTCHA_KEY = "captchaKey";

    public static final String PARAM_CAPTCHA_CODE = "captchaCode";

    public static final String PARAM_TOTP_CODE = "totpCode";

    private final String loginProcessingUrl;
    private final LoginCaptchaService captchaService;
    private final LoginAttemptService attemptService;
    private final MfaService mfaService;
    private final UaaUserService userService;
    private final AuthenticationFailureHandler failureHandler;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !HttpMethod.POST.matches(request.getMethod())
                || !loginProcessingUrl.equals(pathWithinApplication(request));
    }

    private String pathWithinApplication(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();
        return contextPath == null || contextPath.isEmpty() ? uri : uri.substring(contextPath.length());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            check(request);
        } catch (AuthenticationException exception) {
            failureHandler.onAuthenticationFailure(request, response, exception);
            return;
        }
        chain.doFilter(request, response);
    }

    private void check(HttpServletRequest request) {
        if (!captchaService.verify(
                request.getParameter(PARAM_CAPTCHA_KEY), request.getParameter(PARAM_CAPTCHA_CODE))) {
            throw new UaaLoginException("captcha", "图形验证码不正确或已过期");
        }

        String username = request.getParameter("username");
        if (attemptService.isLocked(username)) {
            long remaining = attemptService.lockRemainingSeconds(username);
            throw new UaaLoginException(
                    "locked", "连续登录失败次数过多，请在 " + remaining + " 秒后重试");
        }

        // 用户不存在时不在此处报错，交由密码校验统一返回"用户名或密码错误"，避免账号枚举
        UaaUser user = userService.getByUsername(username);
        if (user != null && user.isLocked()) {
            throw new UaaLoginException("locked", "账号已被锁定，请联系管理员");
        }
        if (user != null
                && user.isMfaEnabled()
                && !mfaService.verify(user, request.getParameter(PARAM_TOTP_CODE))) {
            throw new UaaLoginException("mfa", "动态码不正确");
        }
    }
}
