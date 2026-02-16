package cn.kuma.blog.main.controller;

import cn.kuma.blog.common.model.domain.UserDetail;
import cn.kuma.blog.common.model.result.ApiResult;
import cn.kuma.blog.common.model.result.SystemResultCode;
import cn.kuma.blog.framework.util.JwtUtil;
import cn.kuma.blog.framework.util.UserDetailUtils;
import cn.kuma.blog.main.domain.VO.LoginRequestVO;
import cn.kuma.blog.main.domain.VO.LoginResponseVO;
import cn.kuma.blog.main.domain.entity.User;
import cn.kuma.blog.main.service.TokenService;
import cn.kuma.blog.main.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 提供登录、登出、获取当前用户信息等接口
 * 三级权限说明：
 * - 无需校验：公开接口（如 login）
 * - 普通用户：需要登录
 * - 管理员：需要登录且有管理员权限
 *
 * @author Kuma
 * @version 1.0
 */
@Tag(name = "认证管理", description = "用户认证相关的接口，使用 Redis 存储 Token")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration; // JWT 过期时间（毫秒）

    /** 上 frp/HTTPS 时设为 true，Cookie 仅通过 HTTPS 传输 */
    @Value("${blog.cookie-secure:false}")
    private boolean cookieSecure;

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @param response HTTP 响应对象，用于设置 Cookie
     * @return 登录响应（包含 Token 和用户信息）
     */
    @Operation(summary = "用户登录", description = "通过用户名和密码登录，返回 Token 和用户信息，同时将 Token 写入 Cookie（blog_token）")
    @PostMapping("/login")
    public ApiResult<LoginResponseVO> login(@Valid @RequestBody LoginRequestVO loginRequest, HttpServletResponse response) {
        try {
            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();

            // 查询用户
            User user = userService.getByUsername(username);
            if (user == null) {
                return ApiResult.failed(SystemResultCode.UNAUTHORIZED, "用户名或密码错误");
            }
            // 验证密码（BCrypt）
            if (!userService.checkPassword(password, user.getPassword())) {
                return ApiResult.failed(SystemResultCode.UNAUTHORIZED, "用户名或密码错误");
            }

            // 更新最后登录时间
            userService.updateLastLoginTime(user.getId());

            // 创建用户信息
            UserDetail userDetail = new UserDetail();
            userDetail.setUserID(String.valueOf(user.getId()));
            userDetail.setUserName(user.getNickname() != null ? user.getNickname() : user.getUsername());
            userDetail.setAdminGrade(user.getIsAdmin() != null && user.getIsAdmin() == 1);
            userDetail.setPhone(user.getPhone());

            // 生成 Token
            String token = JwtUtil.generateToken(userDetail);

            // 将 Token 存储到 Redis（过期时间与 JWT 一致）
            long expireSeconds = jwtExpiration / 1000; // 转换为秒
            tokenService.saveToken(token, userDetail, expireSeconds);

            // 将 Token 写入 Cookie
            setTokenCookie(response, token);

            // 构建响应
            LoginResponseVO loginResponse = new LoginResponseVO();
            loginResponse.setToken(token);
            loginResponse.setUserDetail(userDetail);

            return ApiResult.ok(loginResponse);
        } catch (Exception e) {
            return ApiResult.failed(500, "登录失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户信息（用于前端「自动登录」：页面加载时调此接口判断是否已登录）
     * 由 Spring Security 的 TokenAuthenticationFilter 在请求前已从 Cookie blog_token 校验并设置用户，
     * 此处仅读取并返回；未登录时返回 未登录。
     *
     * @return 当前用户信息或未登录
     */
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户信息，用于前端页面加载时判断是否已登录；未登录返回未登录")
    @GetMapping("/current")
    public ApiResult<UserDetail> getCurrentUser() {
        try {
            UserDetail userDetail = UserDetailUtils.getUserDetail();
            if (userDetail == null || userDetail.getUserID() == null) {
                return ApiResult.failed(SystemResultCode.UNAUTHORIZED, "未登录");
            }
            return ApiResult.ok(userDetail);
        } catch (Exception e) {
            return ApiResult.failed(500, "获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 从 Cookie 中获取 blog_token
     *
     * @param request HTTP 请求对象
     * @return token 字符串，不存在则返回 null
     */
    private String getTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if ("blog_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    /**
     * 用户登出
     *
     * @param request  HTTP 请求对象，用于获取 token
     * @param response HTTP 响应对象，用于清除 Cookie
     * @return 登出结果
     */
    @Operation(summary = "用户登出", description = "登出当前用户，从 Redis 删除 token 并清除 Cookie")
    @PostMapping("/logout")
    public ApiResult<String> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 从 Cookie 或请求头获取 token
            String token = getTokenFromCookie(request);

            // 从 Redis 删除 token
            if (StringUtils.hasText(token)) {
                tokenService.deleteToken(token);
            }

            // 清除 Cookie 中的 Token
            clearTokenCookie(response);

            return ApiResult.ok("登出成功");
        } catch (Exception e) {
            return ApiResult.failed(500, "登出失败: " + e.getMessage());
        }
    }

    /**
     * 刷新 Token
     * 延长 Token 的有效期
     *
     * @param request HTTP 请求对象
     * @return 刷新结果
     */
    @Operation(summary = "刷新 Token", description = "刷新当前用户的 Token，延长有效期")
    @PostMapping("/refresh")
    public ApiResult<String> refreshToken(HttpServletRequest request) {
        try {
            String token = getTokenFromCookie(request);

            if (!StringUtils.hasText(token)) {
                return ApiResult.failed(SystemResultCode.UNAUTHORIZED, "Token 不存在");
            }

            // 验证 token 是否有效
            if (!tokenService.validateToken(token)) {
                return ApiResult.failed(SystemResultCode.UNAUTHORIZED, "Token 无效或已过期");
            }

            // 刷新 token 过期时间
            long expireSeconds = jwtExpiration / 1000;
            tokenService.refreshToken(token, expireSeconds);

            return ApiResult.ok("Token 刷新成功");
        } catch (Exception e) {
            return ApiResult.failed(500, "刷新 Token 失败: " + e.getMessage());
        }
    }

    /**
     * 设置 Token Cookie
     *
     * @param response HTTP 响应对象
     * @param token    Token 字符串
     */
    private void setTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("blog_token", token);
        cookie.setHttpOnly(true); // 防止 XSS，JavaScript 无法访问
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setSecure(cookieSecure); // 上 frp/HTTPS 时配置 blog.cookie-secure=true
        response.addCookie(cookie);
    }

    /**
     * 清除 Token Cookie
     *
     * @param response HTTP 响应对象
     */
    private void clearTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("blog_token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setSecure(cookieSecure);
        response.addCookie(cookie);
    }
}
