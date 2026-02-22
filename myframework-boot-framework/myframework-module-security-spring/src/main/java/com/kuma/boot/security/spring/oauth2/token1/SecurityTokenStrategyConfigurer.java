/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.security.config.annotation.web.builders.HttpSecurity
 *  org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
 *  org.springframework.security.core.GrantedAuthority
 *  org.springframework.security.core.authority.SimpleGrantedAuthority
 *  org.springframework.security.oauth2.jwt.Jwt
 *  org.springframework.security.oauth2.jwt.JwtDecoder
 *  org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
 *  org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector
 *  org.springframework.security.oauth2.server.resource.web.BearerTokenResolver
 *  org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver
 *  org.springframework.security.web.AuthenticationEntryPoint
 *  org.springframework.security.web.access.AccessDeniedHandler
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.security.spring.oauth2.token1;

import com.kuma.boot.security.spring.authentication.response.denied.JsonAccessDeniedHandler;
import com.kuma.boot.security.spring.authentication.response.entrypoint.JsonAuthenticationEntryPoint;
import com.kuma.boot.security.spring.enums.Target;
import com.kuma.boot.security.spring.oauth2.authentication.SecurityJwtAuthenticationConverter;
import com.kuma.boot.security.spring.oauth2.introspector.SecurityOpaqueTokenIntrospector;
import com.kuma.boot.security.spring.properties.OAuth2AuthorizationProperties;
import com.kuma.boot.security.spring.properties.OAuth2EndpointProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class SecurityTokenStrategyConfigurer {
    private final JwtDecoder jwtDecoder;
    private final OAuth2AuthorizationProperties authorizationProperties;
    private final OpaqueTokenIntrospector opaqueTokenIntrospector;

    public SecurityTokenStrategyConfigurer(OAuth2AuthorizationProperties authorizationProperties, JwtDecoder jwtDecoder, OAuth2EndpointProperties oAuth2EndpointProperties, OAuth2ResourceServerProperties resourceServerProperties) {
        this.jwtDecoder = jwtDecoder;
        this.authorizationProperties = authorizationProperties;
        this.opaqueTokenIntrospector = new SecurityOpaqueTokenIntrospector(oAuth2EndpointProperties, resourceServerProperties);
    }

    private boolean isRemoteValidate() {
        return this.authorizationProperties.getValidate() == Target.REMOTE;
    }

    public OAuth2ResourceServerConfigurer<HttpSecurity> from(OAuth2ResourceServerConfigurer<HttpSecurity> configurer) {
        if (this.isRemoteValidate()) {
            configurer.opaqueToken(opaqueTokenCustomizer -> opaqueTokenCustomizer.introspector(this.opaqueTokenIntrospector)).accessDeniedHandler((AccessDeniedHandler)new JsonAccessDeniedHandler()).authenticationEntryPoint((AuthenticationEntryPoint)new JsonAuthenticationEntryPoint());
        } else {
            configurer.jwt(jwtCustomizer -> jwtCustomizer.decoder(this.jwtDecoder).jwtAuthenticationConverter((Converter)new SecurityJwtAuthenticationConverter())).bearerTokenResolver((org.springframework.security.oauth2.server.resource.web.BearerTokenResolver)this.bearerTokenResolver()).accessDeniedHandler((AccessDeniedHandler)new JsonAccessDeniedHandler()).authenticationEntryPoint((AuthenticationEntryPoint)new JsonAuthenticationEntryPoint());
        }
        return configurer;
    }

    JwtAuthenticationConverter jwtAuthenticationConverter() {
        CustomJwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new CustomJwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter((Converter)grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    private DefaultBearerTokenResolver bearerTokenResolver() {
        DefaultBearerTokenResolver defaultBearerTokenResolver = new DefaultBearerTokenResolver();
        defaultBearerTokenResolver.setAllowFormEncodedBodyParameter(true);
        defaultBearerTokenResolver.setAllowUriQueryParameter(true);
        return defaultBearerTokenResolver;
    }

    public BearerTokenResolver createBearerTokenResolver() {
        return new SecurityBearerTokenResolver(this.jwtDecoder, this.opaqueTokenIntrospector, this.isRemoteValidate());
    }

    public static class CustomJwtGrantedAuthoritiesConverter
    implements Converter<Jwt, Collection<GrantedAuthority>> {
        private static final String DEFAULT_AUTHORITY_PREFIX = "SCOPE_";
        private static final Collection<String> WELL_KNOWN_AUTHORITIES_CLAIM_NAMES = Arrays.asList("scope", "scp");
        private String authorityPrefix = "SCOPE_";
        private String authoritiesClaimName;

        public Collection<GrantedAuthority> convert(Jwt jwt) {
            ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
            for (String authority : this.getAuthorities(jwt)) {
                grantedAuthorities.add((GrantedAuthority)new SimpleGrantedAuthority(this.authorityPrefix + authority));
            }
            return grantedAuthorities;
        }

        public void setAuthorityPrefix(String authorityPrefix) {
            this.authorityPrefix = authorityPrefix;
        }

        public void setAuthoritiesClaimName(String authoritiesClaimName) {
            Assert.hasText((String)authoritiesClaimName, (String)"authoritiesClaimName cannot be empty");
            this.authoritiesClaimName = authoritiesClaimName;
        }

        private String getAuthoritiesClaimName(Jwt jwt) {
            if (this.authoritiesClaimName != null) {
                return this.authoritiesClaimName;
            }
            for (String claimName : WELL_KNOWN_AUTHORITIES_CLAIM_NAMES) {
                if (!jwt.hasClaim(claimName)) continue;
                return claimName;
            }
            return null;
        }

        private Collection<String> getAuthorities(Jwt jwt) {
            String claimName = this.getAuthoritiesClaimName(jwt);
            if (claimName == null) {
                return Collections.emptyList();
            }
            Object authorities = jwt.getClaim(claimName);
            if (authorities instanceof String) {
                if (StringUtils.hasText((String)((String)authorities))) {
                    return Arrays.asList(((String)authorities).split(" "));
                }
                return Collections.emptyList();
            }
            if (authorities instanceof Collection) {
                Collection collection = (Collection)authorities;
                return collection;
            }
            return Collections.emptyList();
        }

        private Collection<String> castAuthoritiesToCollection(Object authorities) {
            Collection collection = (Collection)authorities;
            return collection;
        }
    }
}

