package com.kuma.boot.sign.web;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 请求体缓存过滤器
 *
 * <p>对 Content-Type 为 {@code application/json} 的 POST/PUT/PATCH 请求，
 * 将 {@link HttpServletRequest} 包装为 {@link BodyCachingRequestWrapper}，
 * 使 InputStream 可被签名校验和业务层各读取一次。
 *
 * <p>需在 Spring MVC DispatcherServlet 之前运行（order 尽可能靠前），
 * 由 {@code SignAutoConfiguration} 注册并设置优先级。
 */
public class SignBodyCachingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest && shouldCacheBody(httpRequest)) {
            chain.doFilter(new BodyCachingRequestWrapper(httpRequest), response);
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean shouldCacheBody(HttpServletRequest request) {
        String method = request.getMethod();
        if (!"POST".equalsIgnoreCase(method) && !"PUT".equalsIgnoreCase(method)
                && !"PATCH".equalsIgnoreCase(method)) {
            return false;
        }
        String contentType = request.getContentType();
        return contentType != null && contentType.contains("application/json");
    }
}
