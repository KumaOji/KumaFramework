package cn.kuma.blog.main.config;

import cn.kuma.blog.framework.util.JsonUtil;
import cn.kuma.blog.common.model.result.ApiResult;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 登录接口限流：按 IP 限制单位时间内的请求次数，防暴力破解。
 *
 * @author Kuma
 * @version 1.0
 */
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/auth/login";
    private static final String METHOD_POST = "POST";

    private final int maxAttempts;
    private final long windowMs;

    /**
     * key: clientIp, value: [count, windowStartMs]
     */
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    public LoginRateLimitFilter(int maxAttempts, long windowSeconds) {
        this.maxAttempts = maxAttempts <= 0 ? 5 : maxAttempts;
        this.windowMs = (windowSeconds <= 0 ? 60 : windowSeconds) * 1000L;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !(METHOD_POST.equalsIgnoreCase(request.getMethod()) && request.getRequestURI() != null
                && request.getRequestURI().endsWith(LOGIN_PATH));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String clientIp = getClientIp(request);
        Window window = windows.computeIfAbsent(clientIp, k -> new Window());

        long now = System.currentTimeMillis();
        int count;
        synchronized (window) {
            if (now - window.startMs >= windowMs) {
                window.startMs = now;
                window.count.set(0);
            }
            count = window.count.incrementAndGet();
        }

        if (count > maxAttempts) {
            response.setStatus(429); // Too Many Requests
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            ApiResult<String> result = ApiResult.failed(429, "登录尝试过于频繁，请稍后再试");
            response.getWriter().write(JsonUtil.toJsonOrNull(result));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private static String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xff)) {
            int comma = xff.indexOf(',');
            return (comma > 0 ? xff.substring(0, comma).trim() : xff.trim());
        }
        String xri = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(xri)) {
            return xri.trim();
        }
        String remote = request.getRemoteAddr();
        return remote != null ? remote : "unknown";
    }

    private static class Window {
        final AtomicInteger count = new AtomicInteger(0);
        long startMs = System.currentTimeMillis();
    }
}
