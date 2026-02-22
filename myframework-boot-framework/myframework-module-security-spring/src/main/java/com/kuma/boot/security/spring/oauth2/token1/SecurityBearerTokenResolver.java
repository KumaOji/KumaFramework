/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal
 *  org.springframework.security.oauth2.jwt.BadJwtException
 *  org.springframework.security.oauth2.jwt.Jwt
 *  org.springframework.security.oauth2.jwt.JwtDecoder
 *  org.springframework.security.oauth2.jwt.JwtException
 *  org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken
 *  org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException
 *  org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException
 *  org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector
 */
package com.kuma.boot.security.spring.oauth2.token1;

import com.kuma.boot.security.spring.core.PrincipalDetails;
import com.kuma.boot.security.spring.utils.PrincipalUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

public class SecurityBearerTokenResolver
implements BearerTokenResolver {
    private static final Logger log = LoggerFactory.getLogger(SecurityBearerTokenResolver.class);
    private final JwtDecoder jwtDecoder;
    private final OpaqueTokenIntrospector opaqueTokenIntrospector;
    private final boolean isRemoteValidate;

    public SecurityBearerTokenResolver(JwtDecoder jwtDecoder, OpaqueTokenIntrospector opaqueTokenIntrospector, boolean isRemoteValidate) {
        this.jwtDecoder = jwtDecoder;
        this.opaqueTokenIntrospector = opaqueTokenIntrospector;
        this.isRemoteValidate = isRemoteValidate;
    }

    @Override
    public PrincipalDetails resolve(String token) {
        if (StringUtils.isBlank((CharSequence)token)) {
            throw new IllegalArgumentException("token can not be null");
        }
        BearerTokenAuthenticationToken bearer = new BearerTokenAuthenticationToken(token);
        if (this.isRemoteValidate) {
            OAuth2AuthenticatedPrincipal principal = this.getOpaque(bearer);
            if (ObjectUtils.isNotEmpty((Object)principal)) {
                PrincipalDetails details = PrincipalUtils.toPrincipalDetails(principal);
                log.info("Resolve OPAQUE token to principal details [{}]", (Object)details);
                return details;
            }
        } else {
            Jwt jwt = this.getJwt(bearer);
            if (ObjectUtils.isNotEmpty((Object)jwt)) {
                PrincipalDetails details = PrincipalUtils.toPrincipalDetails(jwt);
                log.info("Resolve JWT token to principal details [{}]", (Object)details);
                return details;
            }
        }
        return null;
    }

    private Jwt getJwt(BearerTokenAuthenticationToken bearer) {
        try {
            return this.jwtDecoder.decode(bearer.getToken());
        }
        catch (BadJwtException failed) {
            log.info("Failed to decode since the JWT was invalid");
        }
        catch (JwtException failed) {
            log.info("Failed to decode JWT, catch exception", (Throwable)failed);
        }
        return null;
    }

    private OAuth2AuthenticatedPrincipal getOpaque(BearerTokenAuthenticationToken bearer) {
        try {
            return this.opaqueTokenIntrospector.introspect(bearer.getToken());
        }
        catch (BadOpaqueTokenException failed) {
            log.info("Failed to introspect since the Opaque was invalid");
        }
        catch (OAuth2IntrospectionException failed) {
            log.info("Failed to introspect Opaque, catch exception", (Throwable)failed);
        }
        return null;
    }
}

