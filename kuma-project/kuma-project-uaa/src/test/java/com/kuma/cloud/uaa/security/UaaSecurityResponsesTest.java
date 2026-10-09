package com.kuma.cloud.uaa.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;

class UaaSecurityResponsesTest {
    @Test
    void unauthenticatedRequestsUse401AndKeepTheExistingJsonErrorCode() throws Exception {
        var response = new MockHttpServletResponse();
        UaaSecurityResponses.UNAUTHORIZED.commence(new MockHttpServletRequest(), response,
                new InsufficientAuthenticationException("private-authentication-details"));
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).startsWith("application/json");
        assertThat(response.getContentAsString()).contains("401000000000", "FAILURE")
                .doesNotContain("private-authentication-details");
    }

    @Test
    void forbiddenRequestsUse403AndKeepTheExistingJsonErrorCode() throws Exception {
        var response = new MockHttpServletResponse();
        UaaSecurityResponses.FORBIDDEN.handle(new MockHttpServletRequest(), response,
                new AccessDeniedException("private-authorization-details"));
        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentAsString()).contains("403000000000", "FAILURE")
                .doesNotContain("private-authorization-details");
    }
}
