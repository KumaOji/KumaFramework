package com.kuma.cloud.blog.controller;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.blog.domain.entity.User;
import com.kuma.cloud.blog.domain.vo.LoginRequest;
import com.kuma.cloud.blog.domain.vo.LoginResponse;
import com.kuma.cloud.blog.security.LoginRateLimiter;
import com.kuma.cloud.blog.security.TotpAttemptLimiter;
import com.kuma.cloud.blog.service.PermissionService;
import com.kuma.cloud.blog.service.TokenService;
import com.kuma.cloud.blog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;
    private final PermissionService permissionService;
    private final LoginRateLimiter loginRateLimiter;
    private final TotpAttemptLimiter totpAttemptLimiter;

    @Value("${blog.token-expire-seconds:86400}")
    private long tokenExpireSeconds;

    @Value("${blog.cookie-secure:false}")
    private boolean cookieSecure;

    @Value("${blog.totp-issuer:KumaBlog}")
    private String totpIssuer;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                       HttpServletRequest httpRequest, HttpServletResponse response) {
        String ip = LoginRateLimiter.resolveIp(httpRequest);
        loginRateLimiter.check(ip);

        User user = userService.getByUsername(request.getUsername());
        if (user == null || !userService.checkPassword(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // TOTP 验证：账号已绑定时强制要求动态验证码；未绑定时直接放行并提示去绑定
        if (user.getTotpSecret() != null) {
            if (!StringUtils.hasText(request.getTotpCode())) {
                // 密码正确但未携带 TOTP，告知前端需要输入动态验证码
                LoginResponse pending = new LoginResponse();
                pending.setRequireTotp(true);
                return Result.success(pending);
            }
            totpAttemptLimiter.checkLock(user.getId());
            if (!userService.verifyTotp(user.getTotpSecret(), request.getTotpCode())) {
                int remaining = totpAttemptLimiter.recordFailure(user.getId());
                throw new BusinessException("动态验证码错误，本轮还可尝试 " + remaining + " 次");
            }
            totpAttemptLimiter.reset(user.getId());
        }

        loginRateLimiter.reset(ip);
        userService.updateLastLoginTime(user.getId());

        String token = UUID.randomUUID().toString().replace("-", "");
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUserId(user.getId());
        loginResponse.setUsername(user.getUsername());
        loginResponse.setNickname(user.getNickname());
        loginResponse.setAdmin(user.getIsAdmin() != null && user.getIsAdmin() == 1);

        tokenService.saveToken(token, loginResponse, tokenExpireSeconds);
        // 从数据库加载权限（角色权限 + 个人直接授权），写入 Redis
        permissionService.loadAndCachePermissions(user.getId(), user.getUsername(), tokenExpireSeconds);
        setTokenCookie(response, token);
        return Result.success(loginResponse);
    }

    @Operation(summary = "获取当前用户")
    @GetMapping("/current")
    public Result<LoginResponse> current(HttpServletRequest request) {
        String token = getTokenFromCookie(request);
        if (!StringUtils.hasText(token)) {
            throw new BusinessException("未登录");
        }
        LoginResponse lr = tokenService.getLoginResponseByToken(token);
        if (lr == null) {
            throw new BusinessException("未登录");
        }
        return Result.success(lr);
    }

    // ── TOTP ─────────────────────────────────────────────────────

    @Operation(summary = "生成 TOTP 二维码（开启 MFA 第一步）")
    @GetMapping("/totp/setup")
    public Result<String> totpSetup(@AuthenticationPrincipal UserDetails principal) {
        User user = userService.getByUsername(principal.getUsername());
        String qrDataUri = userService.setupTotp(user.getId(), totpIssuer);
        return Result.success(qrDataUri);
    }

    @Operation(summary = "验证并启用 TOTP（扫码后确认）")
    @PostMapping("/totp/enable")
    public Result<String> totpEnable(@AuthenticationPrincipal UserDetails principal,
                                     @RequestParam String code) {
        User user = userService.getByUsername(principal.getUsername());
        userService.enableTotp(user.getId(), code);
        return Result.success("MFA 已启用");
    }

    @Operation(summary = "关闭 TOTP")
    @PostMapping("/totp/disable")
    public Result<String> totpDisable(@AuthenticationPrincipal UserDetails principal,
                                      @RequestParam String code) {
        User user = userService.getByUsername(principal.getUsername());
        userService.disableTotp(user.getId(), code);
        return Result.success("MFA 已关闭");
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = getTokenFromCookie(request);
        if (StringUtils.hasText(token)) {
            LoginResponse lr = tokenService.getLoginResponseByToken(token);
            if (lr != null && lr.getUsername() != null) {
                permissionService.evictCache(lr.getUsername());
            }
            tokenService.deleteToken(token);
        }
        clearTokenCookie(response);
        return Result.success("登出成功");
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<String> refresh(HttpServletRequest request) {
        String token = getTokenFromCookie(request);
        if (!StringUtils.hasText(token) || !tokenService.validateToken(token)) {
            throw new BusinessException("Token无效或已过期");
        }
        LoginResponse lr = tokenService.getLoginResponseByToken(token);
        if (lr != null && lr.getUsername() != null) {
            User user = userService.getByUsername(lr.getUsername());
            if (user != null) {
                permissionService.loadAndCachePermissions(user.getId(), user.getUsername(), tokenExpireSeconds);
            }
        }
        tokenService.refreshToken(token, tokenExpireSeconds);
        return Result.success("Token刷新成功");
    }

    private String getTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("blog_token".equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }

    private void setTokenCookie(HttpServletResponse response, String token) {
        String secureFlag = cookieSecure ? "; Secure" : "";
        response.addHeader("Set-Cookie", String.format(
                "blog_token=%s; HttpOnly%s; SameSite=Strict; Path=/; Max-Age=%d",
                token, secureFlag, tokenExpireSeconds));
    }

    private void clearTokenCookie(HttpServletResponse response) {
        String secureFlag = cookieSecure ? "; Secure" : "";
        response.addHeader("Set-Cookie", String.format(
                "blog_token=; Path=/; Max-Age=0; HttpOnly%s; SameSite=Strict",
                secureFlag));
    }

}
