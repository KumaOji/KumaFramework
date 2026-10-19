package com.kuma.cloud.blog.integration.uaa;

import com.kuma.boot.common.exception.BusinessException;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;

/** UAA APIs may return a Result directly or inside the web response envelope. */
final class UaaResponse {
    static final ParameterizedTypeReference<Map<String, Object>> TYPE = new ParameterizedTypeReference<>() {};

    private UaaResponse() {}

    static Object data(Map<String, Object> response) {
        if (response == null) throw new BusinessException("统一认证中心响应为空，请稍后重试");
        if (response.get("status") instanceof Map<?, ?> status
                && !"0".equals(String.valueOf(status.get("code")))) {
            Object message = status.get("msg");
            throw new BusinessException(message instanceof String text ? text : "统一认证中心请求失败");
        }
        Map<?, ?> result = response.get("payload") instanceof Map<?, ?> payload ? payload : response;
        if (!"SUCCESS".equals(result.get("status"))) {
            Object message = result.get("message");
            throw new BusinessException(message instanceof String text ? text : "统一认证中心请求失败");
        }
        return result.get("data");
    }

    static Map<?, ?> object(Map<String, Object> response) {
        if (data(response) instanceof Map<?, ?> object) return object;
        throw new BusinessException("统一认证中心响应格式异常，请稍后重试");
    }
}
