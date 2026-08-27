package com.kuma.cloud.uaa.support;

import com.kuma.cloud.uaa.domain.entity.UaaLoginLog;
import com.kuma.cloud.uaa.mapper.UaaLoginLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 登录审计落库。审计写入失败不应阻断登录流程，因此异常仅记录日志。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginAuditService {

    private static final int USER_AGENT_MAX_LENGTH = 512;

    private final UaaLoginLogMapper loginLogMapper;

    public void recordSuccess(String username, HttpServletRequest request) {
        record(username, true, null, request);
    }

    public void recordFailure(String username, String failureCode, HttpServletRequest request) {
        record(username, false, failureCode, request);
    }

    /**
     * 优先取网关/反向代理透传的真实客户端地址。
     */
    public static String resolveClientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            int comma = forwarded.indexOf(',');
            return comma > 0 ? forwarded.substring(0, comma).trim() : forwarded.trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        return StringUtils.hasText(realIp) ? realIp.trim() : request.getRemoteAddr();
    }

    private void record(
            String username, boolean success, String failureCode, HttpServletRequest request) {
        try {
            UaaLoginLog entity = new UaaLoginLog();
            entity.setUsername(StringUtils.hasText(username) ? username : "unknown");
            entity.setSuccess(success ? 1 : 0);
            entity.setFailureCode(failureCode);
            entity.setClientIp(resolveClientIp(request));
            entity.setUserAgent(truncate(request == null ? null : request.getHeader("User-Agent")));
            loginLogMapper.insert(entity);
        } catch (RuntimeException exception) {
            log.warn("写入登录审计失败: username={}, {}", username, exception.getMessage());
        }
    }

    private String truncate(String userAgent) {
        if (userAgent == null || userAgent.length() <= USER_AGENT_MAX_LENGTH) {
            return userAgent;
        }
        return userAgent.substring(0, USER_AGENT_MAX_LENGTH);
    }
}
