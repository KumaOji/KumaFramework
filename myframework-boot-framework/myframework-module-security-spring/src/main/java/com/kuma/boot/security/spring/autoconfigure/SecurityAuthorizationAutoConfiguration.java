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

package com.kuma.boot.security.spring.autoconfigure;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.security.spring.access.vip.UrlSecurityPermsLoad;
import com.kuma.boot.security.spring.access.vip.VipSecurityOauthService;
import com.kuma.boot.security.spring.authorization.SecurityAuthorizationManager;
import com.kuma.boot.security.spring.authorization.SecurityMatcherConfigurer;
import com.kuma.boot.security.spring.authorization.SecurityMetadataSourceAnalyzer;
import com.kuma.boot.security.spring.authorization.SecurityMetadataSourceStorage;
import com.kuma.boot.security.spring.authorization.listener.AuthorizationDeniedEventListener;
import com.kuma.boot.security.spring.authorization.listener.AuthorizationGrantedEventListener;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2AuthorizationProperties;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2EndpointProperties;
import com.kuma.boot.security.spring.event.listener.RemoteSecurityMetadataSyncListener;
import com.kuma.boot.security.spring.oauth2.token1.BearerTokenResolver;
import com.kuma.boot.security.spring.oauth2.token1.SecurityTokenStrategyConfigurer;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;

/**
 * Authorization 授权服务器配置 控制哪些用户具有哪些权限来访问应用程序中的特定资源
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:10:46
 */
// @AutoConfiguration(after = JwtDecoderAutoConfiguration.class)
@AutoConfiguration
public class SecurityAuthorizationAutoConfiguration {

    private static final Logger log =
            LoggerFactory.getLogger(SecurityAuthorizationAutoConfiguration.class);

    @PostConstruct
    public void postConstruct() {
        log.info("SDK [OAuth2 Authorization] Auto Configure.");
    }

    /**
     * 从本地文件中 加载 请求路径->权限 规则
     */
    @Configuration
    @ConditionalOnClass(RedisRepository.class)
    public static class UrlSecurityPermsLoadConfiguration {

        @Bean
        @ConditionalOnBean(RedisRepository.class)
        public UrlSecurityPermsLoad urlSecurityPermsLoad(RedisRepository redisRepository) {
            return new UrlSecurityPermsLoad(redisRepository);
        }

        /**
         * 从redis动态加载权限-角色信息
         */
        @Bean
        @ConditionalOnBean(RedisRepository.class)
        public VipSecurityOauthService vipSecurityOauthService(RedisRepository redisRepository) {
            return new VipSecurityOauthService(redisRepository);
        }
    }

    @Bean
    public SecurityMetadataSourceStorage securityMetadataSourceStorage() {
        SecurityMetadataSourceStorage securityMetadataSourceStorage =
                new SecurityMetadataSourceStorage();
        log.info("Bean [Security Metadata Source Storage] Auto Configure.");
        return securityMetadataSourceStorage;
    }

    @Bean
    public SecurityMatcherConfigurer securityMatcherConfigurer(
            OAuth2AuthorizationProperties authorizationProperties) {
        SecurityMatcherConfigurer securityMatcherConfigurer =
                new SecurityMatcherConfigurer(authorizationProperties);
        log.info("Bean [Security Metadata Configurer] Auto Configure.");
        return securityMatcherConfigurer;
    }

    @Bean
    public SecurityAuthorizationManager securityAuthorizationManager(
            SecurityMetadataSourceStorage securityMetadataSourceStorage,
            SecurityMatcherConfigurer securityMatcherConfigurer) {
        SecurityAuthorizationManager securityAuthorizationManager =
                new SecurityAuthorizationManager(
                        securityMetadataSourceStorage, securityMatcherConfigurer);
        log.info("Bean [Authorization Manager] Auto Configure.");
        return securityAuthorizationManager;
    }

    @Bean
    public SecurityMetadataSourceAnalyzer securityMetadataSourceAnalyzer(
            SecurityMetadataSourceStorage securityMetadataSourceStorage,
            SecurityMatcherConfigurer securityMatcherConfigurer) {
        SecurityMetadataSourceAnalyzer securityMetadataSourceAnalyzer =
                new SecurityMetadataSourceAnalyzer(
                        securityMetadataSourceStorage, securityMatcherConfigurer);
        log.info("Bean [Security Metadata Source Analyzer] Auto Configure.");
        return securityMetadataSourceAnalyzer;
    }

    @Bean
    public RemoteSecurityMetadataSyncListener remoteSecurityMetadataSyncListener(
            SecurityMetadataSourceAnalyzer securityMetadataSourceAnalyzer) {
        RemoteSecurityMetadataSyncListener remoteSecurityMetadataSyncListener =
                new RemoteSecurityMetadataSyncListener(securityMetadataSourceAnalyzer);
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
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public SecurityTokenStrategyConfigurer kmcTokenStrategyConfigurer(
            OAuth2AuthorizationProperties authorizationProperties,
            JwtDecoder jwtDecoder,
            OAuth2EndpointProperties oAuth2EndpointProperties,
            OAuth2ResourceServerProperties resourceServerProperties) {
        SecurityTokenStrategyConfigurer securityTokenStrategyConfigurer =
                new SecurityTokenStrategyConfigurer(
                        authorizationProperties,
                        jwtDecoder,
                        oAuth2EndpointProperties,
                        resourceServerProperties);
        log.info("Bean [Token Strategy Configurer] Auto Configure.");
        return securityTokenStrategyConfigurer;
    }

    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnBean(SecurityTokenStrategyConfigurer.class)
    public BearerTokenResolver bearerTokenResolver(
            SecurityTokenStrategyConfigurer securityTokenStrategyConfigurer) {
        BearerTokenResolver bearerTokenResolver =
                securityTokenStrategyConfigurer.createBearerTokenResolver();
        log.info("Bean [Bearer Token Resolver] Auto Configure.");
        return bearerTokenResolver;
    }

    //	@Bean
    //	public AuditorAware<String> auditorAware() {
    //		SecurityAuditorAware securityAuditorAware = new SecurityAuditorAware();
    //		log.info("Bean [Security Auditor Aware] Auto Configure.");
    //		return securityAuditorAware;
    //	}
}
