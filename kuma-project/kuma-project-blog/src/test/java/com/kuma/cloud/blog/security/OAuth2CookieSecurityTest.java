package com.kuma.cloud.blog.security;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OAuth2CookieSecurityTest {

    private final CookieBearerTokenResolver resolver =
            new CookieBearerTokenResolver(OAuth2CookieService.ACCESS_TOKEN_COOKIE);

    @Test
    void resolvesAccessTokenFromHttpOnlyCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(OAuth2CookieService.ACCESS_TOKEN_COOKIE, "cookie-jwt"));

        assertThat(resolver.resolve(request)).isEqualTo("cookie-jwt");
    }

    @Test
    void authorizationHeaderTakesPrecedenceOverCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer header-jwt");
        request.setCookies(new Cookie(OAuth2CookieService.ACCESS_TOKEN_COOKIE, "cookie-jwt"));

        assertThat(resolver.resolve(request)).isEqualTo("header-jwt");
    }

    @Test
    void expiredAccessCookieDoesNotBlockRefreshEndpoint() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/auth/refresh");
        request.setServletPath("/auth/refresh");
        request.setCookies(new Cookie(OAuth2CookieService.ACCESS_TOKEN_COOKIE, "expired-jwt"));

        assertThat(resolver.resolve(request)).isNull();
    }

    @Test
    void writesSecureHttpOnlySameSiteCookies() {
        OAuth2CookieService service = new OAuth2CookieService(true, Duration.ofDays(30));
        MockHttpServletResponse response = new MockHttpServletResponse();

        service.writeTokens(
                response, "access", Instant.now().plusSeconds(300), "refresh");

        List<String> cookies = response.getHeaders("Set-Cookie");
        assertThat(cookies).hasSize(2);
        assertThat(cookies).allSatisfy(cookie -> {
            assertThat(cookie).contains("HttpOnly", "Secure", "SameSite=Strict", "Path=/");
        });
        assertThat(cookies.get(0)).contains(OAuth2CookieService.ACCESS_TOKEN_COOKIE + "=access");
        assertThat(cookies.get(1)).contains(OAuth2CookieService.REFRESH_TOKEN_COOKIE + "=refresh");
    }
}
