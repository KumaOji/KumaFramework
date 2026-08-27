package com.kuma.cloud.uaa.security;

import com.kuma.cloud.uaa.domain.entity.UaaUser;
import com.kuma.cloud.uaa.service.UaaUserService;
import com.kuma.cloud.uaa.support.LoginAttemptService;
import com.kuma.cloud.uaa.support.LoginAuditService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;

/**
 * 登录成功统一出口。
 *
 * <p>继承 {@link SavedRequestAwareAuthenticationSuccessHandler} 是授权码流程的关键：
 * 用户是被 {@code /oauth2/authorize} 重定向到登录页的，登录成功后必须回到被缓存的授权请求，
 * 而不是跳到某个固定首页。
 *
 * @author kuma
 */
@Slf4j
@RequiredArgsConstructor
public class UaaAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UaaUserService userService;
    private final LoginAttemptService attemptService;
    private final LoginAuditService auditService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        String username = authentication.getName();
        attemptService.reset(username);
        auditService.recordSuccess(username, request);

        UaaUser user = userService.getByUsername(username);
        if (user != null) {
            userService.recordLoginSuccess(user.getId(), LoginAuditService.resolveClientIp(request));
        }
        log.info("登录成功: username={}", username);

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
