/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
 */
package com.kuma.boot.security.spring.oauth2.authentication;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

public class SecurityJwtAuthenticationConverter
extends JwtAuthenticationConverter {
    public SecurityJwtAuthenticationConverter() {
        SecurityJwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new SecurityJwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        this.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
    }
}

