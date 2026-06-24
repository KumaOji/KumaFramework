package com.kuma.cloud.blog.controller;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.blog.domain.entity.User;
import com.kuma.cloud.blog.domain.dto.LoginDTO;
import com.kuma.cloud.blog.domain.vo.LoginVO;
import com.kuma.cloud.blog.domain.vo.TotpStatusVO;
import com.kuma.cloud.blog.domain.vo.UserAuthoritiesVO;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO request,
                                       HttpServletRequest httpRequest, HttpServletResponse response) {
        String ip = LoginRateLimiter.resolveIp(httpRequest);
        loginRateLimiter.check(ip);

        User user = userService.getByUsername(request.getUsername());
        if (user == null || !userService.checkPassword(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // TOTP 验证：totp_enabled=1 才视为已启用，否则放行（用户可在登录后完成 setup/enable）
        if (user.getTotpEnabled() != null && user.getTotpEnabled() == 1) {
            if (!StringUtils.hasText(request.getTotpCode())) {
                // 密码正确但未携带 TOTP，告知前端需要输入动态验证码
                LoginVO pending = new LoginVO();
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
        LoginVO loginResponse = new LoginVO();
        loginResponse.setUserId(user.getId());
        loginResponse.setUsername(user.getUsername());
        loginResponse.setNickname(user.getNickname());
        loginResponse.setEmail(user.getEmail());
        loginResponse.setAdmin(user.getIsAdmin() != null && user.getIsAdmin() == 1);

        tokenService.saveToken(token, loginResponse, tokenExpireSeconds);
        // 从数据库加载权限（角色权限 + 个人直接授权），写入 Redis
        permissionService.loadAndCachePermissions(user.getId(), user.getUsername(), tokenExpireSeconds);
        setTokenCookie(response, token);
        return Result.success(loginResponse);
    }

    @Operation(summary = "获取当前用户")
    @GetMapping("/current")
    public Result<LoginVO> current(HttpServletRequest request) {
        String token = getTokenFromCookie(request);
        if (!StringUtils.hasText(token)) {
            throw new BusinessException("未登录");
        }
        LoginVO lr = tokenService.getLoginResponseByToken(token);
        if (lr == null) {
            throw new BusinessException("未登录");
        }
        return Result.success(lr);
    }

    // ── TOTP ─────────────────────────────────────────────────────

    @Operation(summary = "获取当前用户的完整生效权限（角色 + 角色权限 + 直接授权）")
    @GetMapping("/current/authorities")
    public Result<UserAuthoritiesVO> currentAuthorities(@AuthenticationPrincipal UserDetails principal) {
        User user = userService.getByUsername(principal.getUsername());
        return Result.success(permissionService.getUserAuthorities(user.getId()));
    }

    @Operation(summary = "查询当前用户 TOTP 状态")
    @GetMapping("/totp/status")
    public Result<TotpStatusVO> totpStatus(@AuthenticationPrincipal UserDetails principal) {
        User user = userService.getByUsername(principal.getUsername());
        TotpStatusVO resp = new TotpStatusVO();
        resp.setEnabled(user.getTotpEnabled() != null && user.getTotpEnabled() == 1);
        resp.setSecretBound(user.getTotpSecret() != null);
        return Result.success(resp);
    }

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

    @Operation(summary = "解锁 TOTP（管理员用，清除指定用户的验证码锁定状态）")
    @PostMapping("/totp/unlock")
    public Result<String> totpUnlock(@AuthenticationPrincipal UserDetails principal,
                                     @RequestParam Long userId) {
        User operator = userService.getByUsername(principal.getUsername());
        if (operator.getIsAdmin() == null || operator.getIsAdmin() != 1) {
            throw new BusinessException("无权限");
        }
        totpAttemptLimiter.reset(userId);
        return Result.success("已解锁");
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
            LoginVO lr = tokenService.getLoginResponseByToken(token);
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
        LoginVO lr = tokenService.getLoginResponseByToken(token);
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
