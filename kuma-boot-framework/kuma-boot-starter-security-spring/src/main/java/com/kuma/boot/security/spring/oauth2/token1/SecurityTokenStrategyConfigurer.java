/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.security.spring.oauth2.token1;

import com.kuma.boot.security.spring.authentication.response.denied.JsonAccessDeniedHandler;
import com.kuma.boot.security.spring.authentication.response.entrypoint.JsonAuthenticationEntryPoint;
import com.kuma.boot.security.spring.enums.Target;
import com.kuma.boot.security.spring.oauth2.authentication.SecurityJwtAuthenticationConverter;
import com.kuma.boot.security.spring.oauth2.introspector.SecurityOpaqueTokenIntrospector;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2AuthorizationProperties;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2EndpointProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties;
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
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * <p>Token 配置 通用代码
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 09:58:48
 */
public class SecurityTokenStrategyConfigurer {

    /**
     * jwt译码器
     */
    private final JwtDecoder jwtDecoder;

    /**
     * 授权属性
     */
    private final OAuth2AuthorizationProperties authorizationProperties;

    /**
     * 不透明令牌内省
     */
    private final OpaqueTokenIntrospector opaqueTokenIntrospector;

    /**
     * 希罗多德牌策略配置
     *
     * @param authorizationProperties  授权属性
     * @param jwtDecoder               jwt译码器
     * @param oAuth2EndpointProperties 端点属性
     * @param resourceServerProperties 资源服务器属性
     * @since 2023-07-04 09:58:49
     */
    public SecurityTokenStrategyConfigurer(
            OAuth2AuthorizationProperties authorizationProperties,
            JwtDecoder jwtDecoder,
            OAuth2EndpointProperties oAuth2EndpointProperties,
            OAuth2ResourceServerProperties resourceServerProperties) {
        this.jwtDecoder = jwtDecoder;
        this.authorizationProperties = authorizationProperties;
        this.opaqueTokenIntrospector =
                new SecurityOpaqueTokenIntrospector(
                        oAuth2EndpointProperties, resourceServerProperties);
    }

    /**
     * 远程验证
     *
     * @return boolean
     * @since 2023-07-04 09:58:49
     */
    private boolean isRemoteValidate() {
        return this.authorizationProperties.getValidate() == Target.REMOTE;
    }

    /**
     * 从
     *
     * @param configurer 配置
     * @return {@link OAuth2ResourceServerConfigurer }<{@link HttpSecurity }>
     * @since 2023-07-04 09:58:49
     */
    public OAuth2ResourceServerConfigurer<HttpSecurity> from(
            OAuth2ResourceServerConfigurer<HttpSecurity> configurer) {
        if (isRemoteValidate()) {
            configurer
                    .opaqueToken(
                            opaqueTokenCustomizer -> {
                                opaqueTokenCustomizer.introspector(opaqueTokenIntrospector);
                            })
                    .accessDeniedHandler(new JsonAccessDeniedHandler())
                    .authenticationEntryPoint(new JsonAuthenticationEntryPoint());
        } else {
            configurer
                    .jwt(
                            jwtCustomizer -> {
                                jwtCustomizer
                                        .decoder(this.jwtDecoder)
                                        .jwtAuthenticationConverter(
                                                new SecurityJwtAuthenticationConverter());
                                // .jwtAuthenticationConverter(jwtAuthenticationConverter());
                            })
                    .bearerTokenResolver(bearerTokenResolver())
                    .accessDeniedHandler(new JsonAccessDeniedHandler())
                    .authenticationEntryPoint(new JsonAuthenticationEntryPoint());
        }
        return configurer;
    }

    JwtAuthenticationConverter jwtAuthenticationConverter() {
        CustomJwtGrantedAuthoritiesConverter grantedAuthoritiesConverter =
                new CustomJwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    /**
     * 启用参数传递token
     */
    private DefaultBearerTokenResolver bearerTokenResolver() {
        DefaultBearerTokenResolver defaultBearerTokenResolver = new DefaultBearerTokenResolver();
        defaultBearerTokenResolver.setAllowFormEncodedBodyParameter(true);
        defaultBearerTokenResolver.setAllowUriQueryParameter(true);
        return defaultBearerTokenResolver;
    }

    public static class CustomJwtGrantedAuthoritiesConverter
            implements Converter<Jwt, Collection<GrantedAuthority>> {

        private static final String DEFAULT_AUTHORITY_PREFIX = "SCOPE_";

        private static final Collection<String> WELL_KNOWN_AUTHORITIES_CLAIM_NAMES =
                Arrays.asList("scope", "scp");

        private String authorityPrefix = DEFAULT_AUTHORITY_PREFIX;

        private String authoritiesClaimName;

        /**
         * Extract {@link GrantedAuthority}s from the given {@link Jwt}.
         *
         * @param jwt The {@link Jwt} token
         * @return The {@link GrantedAuthority authorities} read from the token scopes
         */
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            for (String authority : getAuthorities(jwt)) {
                grantedAuthorities.add(
                        new SimpleGrantedAuthority(this.authorityPrefix + authority));
            }
            return grantedAuthorities;
        }

        /**
         * Sets the prefix to use for {@link GrantedAuthority authorities} mapped by this converter.
         * Defaults to .
         *
         * @param authorityPrefix The authority prefix
         * @since 5.2
         */
        public void setAuthorityPrefix(String authorityPrefix) {
            // Assert.notNull(authorityPrefix, "authorityPrefix cannot be null");
            this.authorityPrefix = authorityPrefix;
        }

        /**
         * Sets the name of token claim to use for mapping {@link GrantedAuthority authorities} by
         * this converter. Defaults to .
         *
         * @param authoritiesClaimName The token claim name to map authorities
         * @since 5.2
         */
        public void setAuthoritiesClaimName(String authoritiesClaimName) {
            Assert.hasText(authoritiesClaimName, "authoritiesClaimName cannot be empty");
            this.authoritiesClaimName = authoritiesClaimName;
        }

        private String getAuthoritiesClaimName(Jwt jwt) {
            if (this.authoritiesClaimName != null) {
                return this.authoritiesClaimName;
            }
            for (String claimName : WELL_KNOWN_AUTHORITIES_CLAIM_NAMES) {
                if (jwt.hasClaim(claimName)) {
                    return claimName;
                }
            }
            return null;
        }

        private Collection<String> getAuthorities(Jwt jwt) {
            String claimName = getAuthoritiesClaimName(jwt);
            if (claimName == null) {
                return Collections.emptyList();
            }
            Object authorities = jwt.getClaim(claimName);
            if (authorities instanceof String) {
                if (StringUtils.hasText((String) authorities)) {
                    return Arrays.asList(((String) authorities).split(" "));
                }
                return Collections.emptyList();
            }
            if (authorities instanceof Collection) {
                @SuppressWarnings("unchecked")
                Collection<String> collection = (Collection<String>) authorities;
                return collection;
            }
            return Collections.emptyList();
        }

        private Collection<String> castAuthoritiesToCollection(Object authorities) {
            @SuppressWarnings("unchecked")
            Collection<String> collection = (Collection<String>) authorities;
            return collection;
        }
    }

    /**
     * 创建不记名牌解析器
     *
     * @return {@link com.kuma.boot.security.spring.oauth2.token1.BearerTokenResolver }
     * @since 2023-07-04 09:58:49
     */
    public com.kuma.boot.security.spring.oauth2.token1.BearerTokenResolver createBearerTokenResolver() {
        return new com.kuma.boot.security.spring.oauth2.token1.SecurityBearerTokenResolver(
                this.jwtDecoder, this.opaqueTokenIntrospector, this.isRemoteValidate());
    }
}
