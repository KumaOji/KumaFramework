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

import com.kuma.boot.security.spring.core.PrincipalDetails;
import com.kuma.boot.security.spring.utils.PrincipalUtils;
import org.apache.commons.lang3.ObjectUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
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

/**
 * 希罗多德无记名标记解析器
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 09:58:39
 */
public class SecurityBearerTokenResolver implements com.kuma.boot.security.spring.oauth2.token1.BearerTokenResolver {

    /**
     * 日志
     */
    private static final Logger log = LoggerFactory.getLogger(SecurityBearerTokenResolver.class);

    /**
     * jwt译码器
     */
    private final JwtDecoder jwtDecoder;

    /**
     * 不透明令牌内省
     */
    private final OpaqueTokenIntrospector opaqueTokenIntrospector;

    /**
     * 远程验证
     */
    private final boolean isRemoteValidate;

    /**
     * 希罗多德无记名标记解析器
     *
     * @param jwtDecoder              jwt译码器
     * @param opaqueTokenIntrospector 不透明令牌内省
     * @param isRemoteValidate        远程验证
     * @since 2023-07-04 09:58:40
     */
    public SecurityBearerTokenResolver(
            JwtDecoder jwtDecoder,
            OpaqueTokenIntrospector opaqueTokenIntrospector,
            boolean isRemoteValidate) {
        this.jwtDecoder = jwtDecoder;
        this.opaqueTokenIntrospector = opaqueTokenIntrospector;
        this.isRemoteValidate = isRemoteValidate;
    }

    /**
     * 解决
     *
     * @param token 令牌
     * @return {@link PrincipalDetails }
     * @since 2023-07-04 09:58:40
     */
    @Override
    public PrincipalDetails resolve(String token) {
        if (StringUtils.isBlank(token)) {
            throw new IllegalArgumentException("token can not be null");
        }

        BearerTokenAuthenticationToken bearer = new BearerTokenAuthenticationToken(token);

        if (isRemoteValidate) {
            OAuth2AuthenticatedPrincipal principal = getOpaque(bearer);
            if (ObjectUtils.isNotEmpty(principal)) {
                PrincipalDetails details = PrincipalUtils.toPrincipalDetails(principal);
                log.info("Resolve OPAQUE token to principal details [{}]", details);
                return details;
            }
        } else {
            Jwt jwt = getJwt(bearer);
            if (ObjectUtils.isNotEmpty(jwt)) {
                PrincipalDetails details = PrincipalUtils.toPrincipalDetails(jwt);
                log.info("Resolve JWT token to principal details [{}]", details);
                return details;
            }
        }

        return null;
    }

    /**
     * 得到jwt
     *
     * @param bearer 持票人
     * @return {@link Jwt }
     * @since 2023-07-04 09:58:40
     */
    private Jwt getJwt(BearerTokenAuthenticationToken bearer) {
        try {
            return this.jwtDecoder.decode(bearer.getToken());
        } catch (BadJwtException failed) {
            log.info("Failed to decode since the JWT was invalid");
        } catch (JwtException failed) {
            log.info("Failed to decode JWT, catch exception", failed);
        }

        return null;
    }

    /**
     * 得到不透明
     *
     * @param bearer 持票人
     * @return {@link OAuth2AuthenticatedPrincipal }
     * @since 2023-07-04 09:58:40
     */
    private OAuth2AuthenticatedPrincipal getOpaque(BearerTokenAuthenticationToken bearer) {
        try {
            return this.opaqueTokenIntrospector.introspect(bearer.getToken());
        } catch (BadOpaqueTokenException failed) {
            log.info("Failed to introspect since the Opaque was invalid");
        } catch (OAuth2IntrospectionException failed) {
            log.info("Failed to introspect Opaque, catch exception", failed);
        }

        return null;
    }
}
