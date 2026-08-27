package com.kuma.cloud.blog.controller;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.blog.domain.entity.User;
import com.kuma.cloud.blog.domain.vo.LoginVO;
import com.kuma.cloud.blog.domain.vo.UserAuthoritiesVO;
import com.kuma.cloud.blog.security.BlogJwtAuthenticationConverter;
import com.kuma.cloud.blog.security.BlogUserDetails;
import com.kuma.cloud.blog.security.JwtDenylistService;
import com.kuma.cloud.blog.security.OAuth2CookieService;
import com.kuma.cloud.blog.security.OAuth2TokenClient;
import com.kuma.cloud.blog.service.PermissionService;
import com.kuma.cloud.blog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final PermissionService permissionService;
    private final OAuth2CookieService cookieService;
    private final OAuth2TokenClient tokenClient;
    private final JwtDecoder jwtDecoder;
    private final BlogJwtAuthenticationConverter jwtAuthenticationConverter;
    private final JwtDenylistService denylistService;

    @Value("${blog.oauth2.registration-id:blog}")
    private String registrationId;

    @Value("${blog.token-expire-seconds:86400}")
    private long permissionCacheSeconds;

    @Operation(summary = "跳转到 UAA，启动 OAuth2 Authorization Code 登录")
    @GetMapping("/login")
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/oauth2/authorization/" + registrationId);
    }

    @Operation(summary = "获取 Cookie 认证所需的 CSRF Token")
    @GetMapping("/csrf")
    public Result<String> csrf(CsrfToken csrfToken) {
        return Result.success(csrfToken.getToken());
    }

    @Operation(summary = "获取当前用户")
    @GetMapping("/current")
    public Result<LoginVO> current(@AuthenticationPrincipal UserDetails principal) {
        return Result.success(toLoginResponse(currentUser(principal)));
    }

    @Operation(summary = "获取当前用户的完整生效权限（角色 + 角色权限 + 直接授权）")
    @GetMapping("/current/authorities")
    public Result<UserAuthoritiesVO> currentAuthorities(@AuthenticationPrincipal UserDetails principal) {
        User user = currentUser(principal);
        return Result.success(permissionService.getUserAuthorities(user.getId()));
    }

    @Operation(summary = "使用 Refresh Token 换取新的 JWT")
    @PostMapping("/refresh")
    public Result<String> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieService.resolveRefreshToken(request);
        if (!StringUtils.hasText(refreshToken)) {
            throw new BusinessException("Refresh Token 不存在");
        }

        try {
            OAuth2TokenClient.TokenResponse tokens = tokenClient.refresh(refreshToken);
            BlogUserDetails principal = (BlogUserDetails) jwtAuthenticationConverter
                    .convert(jwtDecoder.decode(tokens.accessToken()))
                    .getPrincipal();
            permissionService.loadAndCachePermissions(
                    principal.getLoginResponse().getUserId(),
                    principal.getUsername(),
                    permissionCacheSeconds);
            cookieService.writeTokens(
                    response, tokens.accessToken(), tokens.expiresAt(), tokens.refreshToken());
            return Result.success("Token刷新成功");
        } catch (RuntimeException exception) {
            cookieService.clear(response);
            throw new BusinessException("Refresh Token 无效或已过期");
        }
    }

    @Operation(summary = "撤销 Refresh Token 并清除认证 Cookie")
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = cookieService.resolveAccessToken(request);
        String refreshToken = cookieService.resolveRefreshToken(request);

        if (StringUtils.hasText(accessToken)) {
            try {
                Jwt jwt = jwtDecoder.decode(accessToken);
                BlogUserDetails principal = (BlogUserDetails) jwtAuthenticationConverter
                        .convert(jwt)
                        .getPrincipal();
                denylistService.revoke(jwt);
                permissionService.evictCache(principal.getUsername());
            } catch (RuntimeException exception) {
                log.debug("登出时 Access Token 已无效，无需加入 denylist: {}", exception.getMessage());
            }
            revokeAtUaa(accessToken, "access_token");
        }
        if (StringUtils.hasText(refreshToken)) {
            revokeAtUaa(refreshToken, "refresh_token");
        }

        cookieService.clear(response);
        return Result.success("登出成功");
    }

    private void revokeAtUaa(String token, String tokenTypeHint) {
        try {
            tokenClient.revoke(token, tokenTypeHint);
        } catch (RuntimeException exception) {
            // 本地 denylist 与 Cookie 清理仍保证本次登出有效，UAA 恢复后由短期 JWT/Refresh TTL 兜底。
            log.warn("UAA Token 撤销失败（{}）: {}", tokenTypeHint, exception.getMessage());
        }
    }

    private User currentUser(UserDetails principal) {
        if (principal == null) {
            throw new BusinessException("未登录");
        }
        User user = userService.getByUsername(principal.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    private LoginVO toLoginResponse(User user) {
        LoginVO response = new LoginVO();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setEmail(user.getEmail());
        response.setAdmin(user.getIsAdmin() != null && user.getIsAdmin() == 1);
        return response;
    }
}
