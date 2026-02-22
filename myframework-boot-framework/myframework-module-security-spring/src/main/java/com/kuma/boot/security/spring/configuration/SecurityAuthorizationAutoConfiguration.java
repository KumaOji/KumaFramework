/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  jakarta.annotation.PostConstruct
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnClass
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication$Type
 *  org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.security.oauth2.jwt.JwtDecoder
 */
package com.kuma.boot.security.spring.configuration;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.security.spring.access.vip.UrlSecurityPermsLoad;
import com.kuma.boot.security.spring.access.vip.VipSecurityOauthService;
import com.kuma.boot.security.spring.authorization.SecurityAuthorizationManager;
import com.kuma.boot.security.spring.authorization.SecurityMatcherConfigurer;
import com.kuma.boot.security.spring.authorization.SecurityMetadataSourceAnalyzer;
import com.kuma.boot.security.spring.authorization.SecurityMetadataSourceStorage;
import com.kuma.boot.security.spring.authorization.listener.AuthorizationDeniedEventListener;
import com.kuma.boot.security.spring.authorization.listener.AuthorizationGrantedEventListener;
import com.kuma.boot.security.spring.event.listener.RemoteSecurityMetadataSyncListener;
import com.kuma.boot.security.spring.oauth2.token1.BearerTokenResolver;
import com.kuma.boot.security.spring.oauth2.token1.SecurityTokenStrategyConfigurer;
import com.kuma.boot.security.spring.properties.OAuth2AuthorizationProperties;
import com.kuma.boot.security.spring.properties.OAuth2EndpointProperties;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@AutoConfiguration
public class SecurityAuthorizationAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(SecurityAuthorizationAutoConfiguration.class);

    @PostConstruct
    public void postConstruct() {
        log.info("SDK [OAuth2 Authorization] Auto Configure.");
    }

    @Bean
    public SecurityMetadataSourceStorage securityMetadataSourceStorage() {
        SecurityMetadataSourceStorage securityMetadataSourceStorage = new SecurityMetadataSourceStorage();
        log.info("Bean [Security Metadata Source Storage] Auto Configure.");
        return securityMetadataSourceStorage;
    }

    @Bean
    public SecurityMatcherConfigurer securityMatcherConfigurer(OAuth2AuthorizationProperties authorizationProperties) {
        SecurityMatcherConfigurer securityMatcherConfigurer = new SecurityMatcherConfigurer(authorizationProperties);
        log.info("Bean [Security Metadata Configurer] Auto Configure.");
        return securityMatcherConfigurer;
    }

    @Bean
    public SecurityAuthorizationManager securityAuthorizationManager(SecurityMetadataSourceStorage securityMetadataSourceStorage, SecurityMatcherConfigurer securityMatcherConfigurer) {
        SecurityAuthorizationManager securityAuthorizationManager = new SecurityAuthorizationManager(securityMetadataSourceStorage, securityMatcherConfigurer);
        log.info("Bean [Authorization Manager] Auto Configure.");
        return securityAuthorizationManager;
    }

    @Bean
    public SecurityMetadataSourceAnalyzer securityMetadataSourceAnalyzer(SecurityMetadataSourceStorage securityMetadataSourceStorage, SecurityMatcherConfigurer securityMatcherConfigurer) {
        SecurityMetadataSourceAnalyzer securityMetadataSourceAnalyzer = new SecurityMetadataSourceAnalyzer(securityMetadataSourceStorage, securityMatcherConfigurer);
        log.info("Bean [Security Metadata Source Analyzer] Auto Configure.");
        return securityMetadataSourceAnalyzer;
    }

    @Bean
    public RemoteSecurityMetadataSyncListener remoteSecurityMetadataSyncListener(SecurityMetadataSourceAnalyzer securityMetadataSourceAnalyzer) {
        RemoteSecurityMetadataSyncListener remoteSecurityMetadataSyncListener = new RemoteSecurityMetadataSyncListener(securityMetadataSourceAnalyzer);
        log.info("Bean [Security Metadata Refresh Listener] Auto Configure.");
        return remoteSecurityMetadataSyncListener;
    }

    @Bean
    public AuthorizationDeniedEventListener authorizationDeniedEventListener() {
        return new AuthorizationDeniedEventListener();
    }

    @Bean
    public AuthorizationGrantedEventListener authorizationGrantedEventListener() {
        return new AuthorizationGrantedEventListener();
    }

    @Bean
    @ConditionalOnWebApplication(type=ConditionalOnWebApplication.Type.SERVLET)
    public SecurityTokenStrategyConfigurer ttcTokenStrategyConfigurer(OAuth2AuthorizationProperties authorizationProperties, JwtDecoder jwtDecoder, OAuth2EndpointProperties oAuth2EndpointProperties, OAuth2ResourceServerProperties resourceServerProperties) {
        SecurityTokenStrategyConfigurer securityTokenStrategyConfigurer = new SecurityTokenStrategyConfigurer(authorizationProperties, jwtDecoder, oAuth2EndpointProperties, resourceServerProperties);
        log.info("Bean [Token Strategy Configurer] Auto Configure.");
        return securityTokenStrategyConfigurer;
    }

    @Bean
    @ConditionalOnWebApplication(type=ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnBean(value={SecurityTokenStrategyConfigurer.class})
    public BearerTokenResolver bearerTokenResolver(SecurityTokenStrategyConfigurer securityTokenStrategyConfigurer) {
        BearerTokenResolver bearerTokenResolver = securityTokenStrategyConfigurer.createBearerTokenResolver();
        log.info("Bean [Bearer Token Resolver] Auto Configure.");
        return bearerTokenResolver;
    }

    @Configuration
    @ConditionalOnClass(value={RedisRepository.class})
    public static class UrlSecurityPermsLoadConfiguration {
        @Bean
        @ConditionalOnBean(value={RedisRepository.class})
        public UrlSecurityPermsLoad urlSecurityPermsLoad(RedisRepository redisRepository) {
            return new UrlSecurityPermsLoad(redisRepository);
        }

        @Bean
        @ConditionalOnBean(value={RedisRepository.class})
        public VipSecurityOauthService vipSecurityOauthService(RedisRepository redisRepository) {
            return new VipSecurityOauthService(redisRepository);
        }
    }
}

