/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.jwk.source.JWKSource
 *  com.nimbusds.jose.proc.SecurityContext
 *  org.springframework.beans.factory.BeanFactoryUtils
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.beans.factory.NoUniqueBeanDefinitionException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.core.ResolvableType
 *  org.springframework.security.config.annotation.web.HttpSecurityBuilder
 *  org.springframework.security.oauth2.jwt.JwtEncoder
 *  org.springframework.security.oauth2.jwt.NimbusJwtEncoder
 *  org.springframework.security.provisioning.InMemoryUserDetailsManager
 *  org.springframework.security.provisioning.UserDetailsManager
 *  org.springframework.security.web.authentication.AuthenticationSuccessHandler
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.security.spring.authentication.login.extension.mfa.configure;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.handler.MfaAuthenticationSuccessHandler;
import com.kuma.boot.security.spring.authentication.login.extension.mfa.jwt.JwtGenerator;
import java.util.Map;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
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

public class MfaConfigurerUtils {
    public static <B extends HttpSecurityBuilder<B>> AuthenticationSuccessHandler getAuthenticationSuccessHandler(B builder) {
        UserDetailsManager userDetailsManager;
        JwtEncoder jwtEncoder = (JwtEncoder)builder.getSharedObject(JwtEncoder.class);
        if (jwtEncoder == null) {
            JWKSource<SecurityContext> jwkSource;
            jwtEncoder = MfaConfigurerUtils.getOptionalBean(builder, JwtEncoder.class);
            if (jwtEncoder == null && (jwkSource = MfaConfigurerUtils.getJwkSource(builder)) != null) {
                jwtEncoder = new NimbusJwtEncoder(jwkSource);
            }
            if (jwtEncoder != null) {
                builder.setSharedObject(JwtEncoder.class, (Object)jwtEncoder);
            }
        }
        if ((userDetailsManager = (UserDetailsManager)builder.getSharedObject(UserDetailsManager.class)) == null) {
            userDetailsManager = MfaConfigurerUtils.getOptionalBean(builder, UserDetailsManager.class);
            if (userDetailsManager == null) {
                userDetailsManager = new InMemoryUserDetailsManager();
            }
            builder.setSharedObject(UserDetailsManager.class, (Object)userDetailsManager);
        }
        return new MfaAuthenticationSuccessHandler(new JwtGenerator(jwtEncoder), userDetailsManager);
    }

    static <B extends HttpSecurityBuilder<B>> JWKSource<SecurityContext> getJwkSource(B builder) {
        ResolvableType type;
        JWKSource jwkSource = (JWKSource)builder.getSharedObject(JWKSource.class);
        if (jwkSource == null && (jwkSource = (JWKSource)MfaConfigurerUtils.getOptionalBean(builder, type = ResolvableType.forClassWithGenerics(JWKSource.class, (Class[])new Class[]{SecurityContext.class}))) != null) {
            builder.setSharedObject(JWKSource.class, (Object)jwkSource);
        }
        return jwkSource;
    }

    static <B extends HttpSecurityBuilder<B>, T> T getOptionalBean(B builder, Class<T> type) {
        Map beansMap = BeanFactoryUtils.beansOfTypeIncludingAncestors((ListableBeanFactory)((ListableBeanFactory)builder.getSharedObject(ApplicationContext.class)), type);
        if (beansMap.size() > 1) {
            throw new NoUniqueBeanDefinitionException(type, beansMap.size(), "Expected single matching bean of type '" + type.getName() + "' but found " + beansMap.size() + ": " + StringUtils.collectionToCommaDelimitedString(beansMap.keySet()));
        }
        return !beansMap.isEmpty() ? (T)beansMap.values().iterator().next() : null;
    }

    static <B extends HttpSecurityBuilder<B>, T> T getOptionalBean(B builder, ResolvableType type) {
        ApplicationContext context = (ApplicationContext)builder.getSharedObject(ApplicationContext.class);
        String[] names = context.getBeanNamesForType(type);
        if (names.length > 1) {
            throw new NoUniqueBeanDefinitionException(type, names);
        }
        return (T)(names.length == 1 ? context.getBean(names[0]) : null);
    }
}

