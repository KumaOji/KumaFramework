package com.kuma.boot.security.spring.access.expression;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizeCheckServiceTest {

    private final AuthorizeCheckService service = new AuthorizeCheckService(null);

    @Test
    void adminRolePassesModulePermission() throws Exception {
        Method method = AuthorizeCheckService.class.getDeclaredMethod(
                "hasAuthority", Set.class, String.class);
        method.setAccessible(true);

        boolean allowed = (boolean) method.invoke(
                service, Set.of(RoleConstants.ADMIN, RoleConstants.USER), "ai_chat:ingest");

        assertThat(allowed).isTrue();
    }

    @Test
    void mergesAuthenticationAuthoritiesWhenRedisMissing() throws Exception {
        Method method = AuthorizeCheckService.class.getDeclaredMethod(
                "getAuthorities", String.class, org.springframework.security.core.Authentication.class);
        method.setAccessible(true);
        var authentication = new UsernamePasswordAuthenticationToken(
                "admin", "n/a", List.of(new SimpleGrantedAuthority(RoleConstants.ADMIN)));

        @SuppressWarnings("unchecked")
        List<String> authorities = (List<String>) method.invoke(service, "admin", authentication);

        assertThat(authorities).contains(RoleConstants.ADMIN);
    }
}
