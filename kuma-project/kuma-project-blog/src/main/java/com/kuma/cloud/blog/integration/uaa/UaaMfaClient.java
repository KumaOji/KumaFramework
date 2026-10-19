package com.kuma.cloud.blog.integration.uaa;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2EndpointProperties;
import com.kuma.cloud.blog.domain.vo.TotpSetupVO;
import com.kuma.cloud.blog.domain.vo.TotpStatusVO;
import com.kuma.cloud.blog.security.OAuth2CookieService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Forward MFA operations using the current user's token; UAA owns the binding. */
@Component
public class UaaMfaClient {
    private final RestClient restClient;
    private final OAuth2EndpointProperties endpoints;
    private final OAuth2CookieService cookies;

    public UaaMfaClient(RestClient.Builder builder, OAuth2EndpointProperties endpoints, OAuth2CookieService cookies) {
        this.restClient = builder.build();
        this.endpoints = endpoints;
        this.cookies = cookies;
    }

    public TotpStatusVO status(HttpServletRequest request) {
        var data = UaaResponse.object(get(request, "/status"));
        if (!(data.get("enabled") instanceof Boolean enabled)
                || !(data.get("secretBound") instanceof Boolean bound)) {
            throw new BusinessException("统一认证中心未返回绑定状态，请稍后重试");
        }
        TotpStatusVO vo = new TotpStatusVO();
        vo.setFeatureEnabled(Boolean.TRUE.equals(data.get("featureEnabled")));
        vo.setEnabled(enabled);
        vo.setSecretBound(bound);
        return vo;
    }

    public TotpSetupVO setup(HttpServletRequest request) {
        var data = UaaResponse.object(get(request, "/bind"));
        if (!(data.get("qrImage") instanceof String qr) || !qr.startsWith("data:image/")) {
            throw new BusinessException("统一认证中心未返回绑定二维码，请稍后重试");
        }
        TotpSetupVO vo = new TotpSetupVO();
        vo.setQrDataUri(qr);
        return vo;
    }

    public void enable(HttpServletRequest request, String code) { post(request, "/bind", code); }
    public void disable(HttpServletRequest request, String code) { post(request, "/unbind", code); }

    private Map<String, Object> get(HttpServletRequest request, String path) {
        try {
            return restClient.get().uri(uri(path)).headers(h -> h.setBearerAuth(token(request)))
                    .retrieve().body(UaaResponse.TYPE);
        } catch (RestClientException exception) {
            throw new BusinessException("无法读取统一认证中心的绑定信息，请稍后重试");
        }
    }

    private void post(HttpServletRequest request, String path, String code) {
        try {
            UaaResponse.data(restClient.post().uri(uri(path)).headers(h -> h.setBearerAuth(token(request)))
                    .contentType(MediaType.APPLICATION_JSON).body(Map.of("code", code))
                    .retrieve().body(UaaResponse.TYPE));
        } catch (RestClientException exception) {
            throw new BusinessException("无法更新统一认证中心的绑定信息，请稍后重试");
        }
    }

    private String uri(String path) { return endpoints.getUaaServiceUri() + "/api/me/mfa" + path; }

    private String token(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String token = header != null && header.regionMatches(true, 0, "Bearer ", 0, 7)
                ? header.substring(7).trim() : cookies.resolveAccessToken(request);
        if (token == null || token.isBlank()) throw new BusinessException("登录已过期，请重新登录");
        return token;
    }
}
