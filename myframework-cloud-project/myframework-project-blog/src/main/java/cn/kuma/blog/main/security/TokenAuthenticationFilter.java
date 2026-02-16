package cn.kuma.blog.main.security;

import cn.kuma.blog.common.model.domain.UserDetail;
import cn.kuma.blog.framework.util.JwtUtil;
import cn.kuma.blog.framework.util.UserDetailUtils;
import cn.kuma.blog.main.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 从 Cookie blog_token 读取 Token，校验后设置 SecurityContext 与 request 属性（兼容 UserDetailUtils）。
 *
 * @author Kuma
 * @version 1.0
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_COOKIE = "blog_token";

    private final TokenService tokenService;

    public TokenAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);
        if (StringUtils.hasText(token)) {
            UserDetail userDetail = validateAndGetUserDetail(token);
            if (userDetail != null && userDetail.getUserID() != null) {
                BlogUserDetails userDetails = new BlogUserDetails(userDetail);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                request.setAttribute(UserDetailUtils.USER_DETAIL, userDetail);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (TOKEN_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private UserDetail validateAndGetUserDetail(String token) {
        if (tokenService != null) {
            UserDetail userDetail = tokenService.getUserDetailByToken(token);
            if (userDetail != null && userDetail.getUserID() != null) {
                return userDetail;
            }
        }
        try {
            if (JwtUtil.validateToken(token)) {
                return JwtUtil.parseToken(token);
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
