package com.kuma.cloud.blog.integration.uaa;

import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2EndpointProperties;
import com.kuma.cloud.blog.security.OAuth2CookieService;
import jakarta.servlet.http.Cookie;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class UaaMfaClientTest {
    @Test
    void readsUaaBindingAndWritesToUaaUsingTheCurrentUserToken() {
        RestClient.Builder builder = RestClient.builder();
        var server = MockRestServiceServer.bindTo(builder).build();
        var endpoints = new OAuth2EndpointProperties();
        endpoints.setUaaServiceUri("http://uaa.test");
        var client = new UaaMfaClient(builder, endpoints, new OAuth2CookieService(true, Duration.ofDays(30)));
        var request = new MockHttpServletRequest();
        request.setCookies(new Cookie(OAuth2CookieService.ACCESS_TOKEN_COOKIE, "current-user-token"));
        server.expect(requestTo("http://uaa.test/api/me/mfa/status"))
                .andExpect(header("Authorization", "Bearer current-user-token"))
                .andRespond(withSuccess("""
                        {"payload":{"status":"SUCCESS","data":{"featureEnabled":true,"enabled":true,"secretBound":true}}}
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo("http://uaa.test/api/me/mfa/unbind"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer current-user-token"))
                .andExpect(content().json("{\"code\":\"123456\"}"))
                .andRespond(withSuccess("{\"status\":\"SUCCESS\",\"data\":\"ok\"}", MediaType.APPLICATION_JSON));
        assertThat(client.status(request).isEnabled()).isTrue();
        client.disable(request, "123456");
        server.verify();
    }

    @Test
    void preservesRejectedDynamicCodeInsteadOfReportingSuccess() {
        RestClient.Builder builder = RestClient.builder();
        var server = MockRestServiceServer.bindTo(builder).build();
        var endpoints = new OAuth2EndpointProperties();
        endpoints.setUaaServiceUri("http://uaa.test");
        var client = new UaaMfaClient(builder, endpoints, new OAuth2CookieService(true, Duration.ofDays(30)));
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer current-user-token");
        server.expect(requestTo("http://uaa.test/api/me/mfa/bind"))
                .andRespond(withSuccess("{\"payload\":{\"status\":\"FAILURE\",\"message\":\"动态码不正确\"}}", MediaType.APPLICATION_JSON));
        assertThatThrownBy(() -> client.enable(request, "000000"))
                .isInstanceOf(BusinessException.class).hasMessage("动态码不正确");
        server.verify();
    }
}
