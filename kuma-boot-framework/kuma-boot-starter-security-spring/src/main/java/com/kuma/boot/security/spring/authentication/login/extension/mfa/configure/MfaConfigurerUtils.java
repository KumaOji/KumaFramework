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

package com.kuma.boot.security.spring.authentication.login.extension.mfa.configure;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.handler.MfaAuthenticationSuccessHandler;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.jwt.JwtGenerator;
import java.util.Map;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.StringUtils;

/**
 * @author: ReLive27
 * @since: 2023/2/2 19:31
 */
public class MfaConfigurerUtils {

    public static <B extends HttpSecurityBuilder<B>>
    AuthenticationSuccessHandler getAuthenticationSuccessHandler(B builder) {
        JwtEncoder jwtEncoder = builder.getSharedObject(JwtEncoder.class);
        if (jwtEncoder == null) {
            jwtEncoder = getOptionalBean(builder, JwtEncoder.class);
            if (jwtEncoder == null) {
                JWKSource<SecurityContext> jwkSource = getJwkSource(builder);
                if (jwkSource != null) {
                    jwtEncoder = new NimbusJwtEncoder(jwkSource);
                }
            }
            if (jwtEncoder != null) {
                builder.setSharedObject(JwtEncoder.class, jwtEncoder);
            }
        }

        UserDetailsManager userDetailsManager = builder.getSharedObject(UserDetailsManager.class);
        if (userDetailsManager == null) {
            userDetailsManager = getOptionalBean(builder, UserDetailsManager.class);
            if (userDetailsManager == null) {
                userDetailsManager = new InMemoryUserDetailsManager();
            }
            builder.setSharedObject(UserDetailsManager.class, userDetailsManager);
        }
        return new MfaAuthenticationSuccessHandler(
                new JwtGenerator(jwtEncoder), userDetailsManager);
    }

    @SuppressWarnings("unchecked")
    static <B extends HttpSecurityBuilder<B>> JWKSource<SecurityContext> getJwkSource(B builder) {
        JWKSource<SecurityContext> jwkSource = builder.getSharedObject(JWKSource.class);
        if (jwkSource == null) {
            ResolvableType type =
                    ResolvableType.forClassWithGenerics(JWKSource.class, SecurityContext.class);
            jwkSource = getOptionalBean(builder, type);
            if (jwkSource != null) {
                builder.setSharedObject(JWKSource.class, jwkSource);
            }
        }
        return jwkSource;
    }

    static <B extends HttpSecurityBuilder<B>, T> T getOptionalBean(B builder, Class<T> type) {
        Map<String, T> beansMap =
                BeanFactoryUtils.beansOfTypeIncludingAncestors(
                        builder.getSharedObject(ApplicationContext.class), type);
        if (beansMap.size() > 1) {
            throw new NoUniqueBeanDefinitionException(
                    type,
                    beansMap.size(),
                    "Expected single matching bean of type '"
                            + type.getName()
                            + "' but found "
                            + beansMap.size()
                            + ": "
                            + StringUtils.collectionToCommaDelimitedString(beansMap.keySet()));
        }
        return (!beansMap.isEmpty() ? beansMap.values().iterator().next() : null);
    }

    @SuppressWarnings("unchecked")
    static <B extends HttpSecurityBuilder<B>, T> T getOptionalBean(B builder, ResolvableType type) {
        ApplicationContext context = builder.getSharedObject(ApplicationContext.class);
        String[] names = context.getBeanNamesForType(type);
        if (names.length > 1) {
            throw new NoUniqueBeanDefinitionException(type, names);
        }
        return names.length == 1 ? (T) context.getBean(names[0]) : null;
    }
}
