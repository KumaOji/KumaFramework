package com.kuma.cloud.blog.controller;

import com.kuma.cloud.blog.integration.uaa.UaaAuthSettingsService;
import com.kuma.cloud.blog.security.BlogJwtAuthenticationConverter;
import com.kuma.cloud.blog.security.JwtDenylistService;
import com.kuma.cloud.blog.security.OAuth2CookieService;
import com.kuma.cloud.blog.security.OAuth2TokenClient;
import com.kuma.cloud.blog.service.PermissionService;
import com.kuma.cloud.blog.service.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AuthControllerLogoutTest {

    private JwtDenylistService denylistService;
    private OAuth2TokenClient tokenClient;
    private AuthController controller;
    private OAuth2CookieService cookieService;

    @BeforeEach
    void setUp() {
        denylistService = mock(JwtDenylistService.class);
        tokenClient = mock(OAuth2TokenClient.class);
        cookieService = new OAuth2CookieService(true, Duration.ofDays(30));
        controller = new AuthController(
                mock(UserService.class),
                mock(PermissionService.class),
                cookieService,
                tokenClient,
                mock(JwtDecoder.class),
                mock(BlogJwtAuthenticationConverter.class),
                denylistService,
                mock(UaaAuthSettingsService.class));
        ReflectionTestUtils.setField(controller, "registrationId", "blog");
        ReflectionTestUtils.setField(controller, "permissionCacheSeconds", 86400L);
        ReflectionTestUtils.setField(controller, "issuerUri", "https://uaa.kumacloud.top");
    }

    @Test
    void logoutRevokesRefreshTokenAndClearsBothCookies() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(
                new Cookie(OAuth2CookieService.ACCESS_TOKEN_COOKIE, "access-jwt"),
                new Cookie(OAuth2CookieService.REFRESH_TOKEN_COOKIE, "refresh-opaque"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.logout(request, response);

        verify(denylistService).revokeRefreshToken("refresh-opaque");
        verify(tokenClient).revoke(eq("refresh-opaque"), eq("refresh_token"));

        List<String> cookies = response.getHeaders("Set-Cookie");
        assertThat(cookies).hasSize(2);
        assertThat(cookies.get(0)).contains(OAuth2CookieService.ACCESS_TOKEN_COOKIE + "=", "Max-Age=0");
        assertThat(cookies.get(1)).contains(OAuth2CookieService.REFRESH_TOKEN_COOKIE + "=", "Max-Age=0");
        assertThat(response.getHeader("X-UAA-Logout-Url")).isEqualTo("https://uaa.kumacloud.top/logout");
    }

    @Test
    void logoutRedirectClearsCookiesAndSendsUserToUaaLogout() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(OAuth2CookieService.REFRESH_TOKEN_COOKIE, "refresh-opaque"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        var result = controller.logoutRedirect(request, response);

        verify(denylistService).revokeRefreshToken("refresh-opaque");
        assertThat(result.getStatusCode().value()).isEqualTo(302);
        assertThat(result.getHeaders().getLocation())
                .hasToString("https://uaa.kumacloud.top/logout");
        assertThat(response.getHeaders("Set-Cookie")).hasSize(2);
    }
}
