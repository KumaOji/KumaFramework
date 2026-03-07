package com.kuma.cloud.blog.controller;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.result.Result;
import com.kuma.cloud.blog.domain.entity.User;
import com.kuma.cloud.blog.domain.vo.LoginRequest;
import com.kuma.cloud.blog.domain.vo.LoginResponse;
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

    @Value("${blog.token-expire-seconds:86400}")
    private long tokenExpireSeconds;

    @Value("${blog.cookie-secure:false}")
    private boolean cookieSecure;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        User user = userService.getByUsername(request.getUsername());
        if (user == null || !userService.checkPassword(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        userService.updateLastLoginTime(user.getId());

        String token = UUID.randomUUID().toString().replace("-", "");
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
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
        Cookie cookie = new Cookie("blog_token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setSecure(cookieSecure);
        response.addCookie(cookie);
    }

    private void clearTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("blog_token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setSecure(cookieSecure);
        response.addCookie(cookie);
    }

}
