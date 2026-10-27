package com.kuma.cloud.blog.security;

import com.kuma.cloud.blog.domain.entity.User;
import com.kuma.cloud.blog.service.PermissionService;
import com.kuma.cloud.blog.service.UserService;
import com.kuma.boot.auditlog.core.ServletAuthenticationAuditRecorder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final OAuth2CookieService cookieService;
    private final UserService userService;
    private final PermissionService permissionService;
    private final ServletAuthenticationAuditRecorder audit;
    private final String usernameClaim;
    private final String successUrl;
    private final long permissionCacheSeconds;

    public OAuth2LoginSuccessHandler(
            OAuth2AuthorizedClientService authorizedClientService,
            OAuth2CookieService cookieService,
            UserService userService,
            PermissionService permissionService,
            ServletAuthenticationAuditRecorder audit,
            @Value("${blog.oauth2.username-claim:preferred_username}") String usernameClaim,
            @Value("${blog.oauth2.login-success-url:/}") String successUrl,
            @Value("${blog.token-expire-seconds:86400}") long permissionCacheSeconds) {
        this.authorizedClientService = authorizedClientService;
        this.cookieService = cookieService;
        this.userService = userService;
        this.permissionService = permissionService;
        this.audit = audit;
        this.usernameClaim = usernameClaim;
        this.successUrl = successUrl;
        this.permissionCacheSeconds = permissionCacheSeconds;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        if (!(authentication instanceof OAuth2AuthenticationToken oauth)) {
            audit.record("blog.authentication", null, false, "invalid_oauth_state", request);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "OAuth2 登录状态无效");
            return;
        }

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauth.getAuthorizedClientRegistrationId(), oauth.getName());
        if (client == null || client.getAccessToken() == null) {
            audit.record("blog.authentication", null, false, "missing_access_token", request);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UAA 未返回 Access Token");
            return;
        }

        String username = oauth.getPrincipal().getAttribute(usernameClaim);
        if (!StringUtils.hasText(username)) {
            username = oauth.getPrincipal().getName();
        }
        User user = userService.getByUsername(username);
        if (user == null || (user.getStatus() != null && user.getStatus() == 0)) {
            audit.record("blog.authentication", null, false, "blog_user_unavailable", request);
            authorizedClientService.removeAuthorizedClient(
                    oauth.getAuthorizedClientRegistrationId(), oauth.getName());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "UAA 用户未在 Blog 中启用");
            return;
        }

        userService.updateLastLoginTime(user.getId());
        permissionService.loadAndCachePermissions(
                user.getId(), user.getUsername(), permissionCacheSeconds);
        cookieService.writeTokens(
                response,
                client.getAccessToken().getTokenValue(),
                client.getAccessToken().getExpiresAt(),
                client.getRefreshToken() == null ? null : client.getRefreshToken().getTokenValue());

        authorizedClientService.removeAuthorizedClient(
                oauth.getAuthorizedClientRegistrationId(), oauth.getName());
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.setStatus(HttpServletResponse.SC_FOUND);
        audit.record("blog.authentication", user.getUsername(), true, "login_success", request);
        response.setHeader("Location", response.encodeRedirectURL(successUrl));
    }
}
