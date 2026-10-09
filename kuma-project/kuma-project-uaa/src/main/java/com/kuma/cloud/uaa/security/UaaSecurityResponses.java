package com.kuma.cloud.uaa.security;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.json.JacksonUtils;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

/** Preserve UAA's JSON error contract while exposing the real HTTP status to clients and metrics. */
public final class UaaSecurityResponses {
    private UaaSecurityResponses() {}

    public static final AuthenticationEntryPoint UNAUTHORIZED = (request, response, error) ->
            write(response, HttpServletResponse.SC_UNAUTHORIZED, ResultEnum.UNAUTHORIZED);
    public static final AccessDeniedHandler FORBIDDEN = (request, response, error) ->
            write(response, HttpServletResponse.SC_FORBIDDEN, ResultEnum.FORBIDDEN);

    private static void write(HttpServletResponse response, int status, ResultEnum result) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JacksonUtils.toJSONString(Result.fail(result)));
    }
}
