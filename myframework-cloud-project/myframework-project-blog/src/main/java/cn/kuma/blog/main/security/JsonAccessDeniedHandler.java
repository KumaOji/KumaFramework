package cn.kuma.blog.main.security;

import cn.kuma.blog.common.model.result.ApiResult;
import cn.kuma.blog.common.model.result.SystemResultCode;
import cn.kuma.blog.framework.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * 无权限时返回 403 + ApiResult JSON。
 */
public class JsonAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ApiResult<String> result = ApiResult.failed(SystemResultCode.FORBIDDEN, "权限不足");
        response.getWriter().write(JsonUtil.toJsonOrNull(result));
    }
}
