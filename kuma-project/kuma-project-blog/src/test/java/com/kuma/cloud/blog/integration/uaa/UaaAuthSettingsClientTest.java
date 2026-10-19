package com.kuma.cloud.blog.integration.uaa;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2EndpointProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class UaaAuthSettingsClientTest {
    @Test
    void readsBothDirectAndWrappedResultsWithoutSilentlyDisablingMfa() {
        String result = """
                {"status":"SUCCESS","data":{"mfaEnabled":true,"captchaEnabled":true,"mfaIssuer":"KumaCloud"}}
                """;
        for (String body : new String[]{result, "{\"payload\":" + result + ",\"status\":{\"code\":\"0\"}}"}) {
            RestClient.Builder builder = RestClient.builder();
            var server = MockRestServiceServer.bindTo(builder).build();
            var endpoints = new OAuth2EndpointProperties();
            endpoints.setUaaServiceUri("http://uaa.test");
            server.expect(requestTo("http://uaa.test/api/public/auth-settings"))
                    .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));
            var settings = new UaaAuthSettingsClient(builder, endpoints).fetch();
            assertThat(settings.isMfaEnabled()).isTrue();
            assertThat(settings.getMfaIssuer()).isEqualTo("KumaCloud");
            server.verify();
        }
    }

    @Test
    void unavailableSettingsAreAnErrorRatherThanDisabled() {
        RestClient.Builder builder = RestClient.builder();
        var server = MockRestServiceServer.bindTo(builder).build();
        var endpoints = new OAuth2EndpointProperties();
        endpoints.setUaaServiceUri("http://uaa.test");
        server.expect(requestTo("http://uaa.test/api/public/auth-settings")).andRespond(withServerError());
        assertThatThrownBy(() -> new UaaAuthSettingsClient(builder, endpoints).fetch())
                .isInstanceOf(BusinessException.class).hasMessageContaining("暂时无法获取");
        server.verify();
    }

    @Test
    void missingOrFailedSettingsDoNotMasqueradeAsDisabled() {
        assertThatThrownBy(() -> UaaResponse.object(java.util.Map.of("status", "FAILURE", "message", "配置读取失败")))
                .isInstanceOf(BusinessException.class).hasMessage("配置读取失败");
        assertThatThrownBy(() -> UaaResponse.object(java.util.Map.of("status", "SUCCESS")))
                .isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> UaaResponse.data(java.util.Map.of("payload", java.util.Map.of(),
                "status", java.util.Map.of("code", "1", "msg", "动态码不正确"))))
                .isInstanceOf(BusinessException.class).hasMessage("动态码不正确");
    }
}
