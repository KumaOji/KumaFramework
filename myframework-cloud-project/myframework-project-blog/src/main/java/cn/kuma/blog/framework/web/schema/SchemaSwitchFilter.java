package cn.kuma.blog.framework.web.schema;

import cn.kuma.blog.framework.mybatisplus.util.SchemaContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Schema 动态切换过滤器
 * 从请求头 X-Schema 读取目标 schema，设置到 {@link SchemaContext}，请求结束后清除
 * 需配合 {@link cn.kuma.blog.framework.mybatisplus.interceptor.SchemaSwitchInterceptor} 使用
 *
 * @author Kuma
 * @version 1.0
 */
public class SchemaSwitchFilter extends OncePerRequestFilter {

    public static final String HEADER_X_SCHEMA = "X-Schema";

    private final String headerName;

    public SchemaSwitchFilter() {
        this(HEADER_X_SCHEMA);
    }

    public SchemaSwitchFilter(String headerName) {
        this.headerName = headerName != null && !headerName.isBlank() ? headerName.trim() : HEADER_X_SCHEMA;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String schema = request.getHeader(headerName);
        if (schema != null && !schema.isBlank()) {
            SchemaContext.setSchema(schema.trim());
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            SchemaContext.clearSchema();
        }
    }
}
