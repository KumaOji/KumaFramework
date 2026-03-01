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
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.spring.autoconfigure.properties.OAuth2AuthenticationProperties;
import com.kuma.boot.security.spring.oauth2.token.OAuth2AccessTokenStore;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>OAuth2 认证基础模块配置 </p>
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-10 17:14:18
 */
@AutoConfiguration
@EnableConfigurationProperties({OAuth2AuthenticationProperties.class})
public class SecurityAuthenticationAutoConfiguration {

    /**
     * 后期构建
     *
     * @since 2023-07-10 17:14:18
     */
    @PostConstruct
    public void postConstruct() {
        LogUtils.info("SDK [OAuth2 Authentication] Auto Configure.");
    }

    /**
     * 从本地文件中 加载 请求路径->权限 规则
     */
    @Configuration
    @ConditionalOnClass(RedisRepository.class)
    public static class OAuth2AccessTokenStoreConfiguration {

        @Bean
        @ConditionalOnBean(RedisRepository.class)
        public OAuth2AccessTokenStore oAuth2AccessTokenStore(RedisRepository redisRepository) {
            return new OAuth2AccessTokenStore(redisRepository);
        }
    }

    //	/**
    //	 * 锁定用户详细信息邮票管理器
    //	 *
    //	 * @param authenticationProperties 身份验证属性
    //	 * @return {@link LockedUserDetailsStampManager }
    //	 * @since 2023-07-10 17:14:20
    //	 */
    //	@Bean
    //	public LockedUserDetailsStampManager lockedUserDetailsStampManager(
    //		OAuth2AuthenticationProperties authenticationProperties) {
    //		LockedUserDetailsStampManager manager =
    //			new LockedUserDetailsStampManager(redisRepository, authenticationProperties);
    //		LogUtils.info("Bean [Locked UserDetails Stamp Manager] Auto Configure.");
    //		return manager;
    //	}

    //	/**
    //	 * 登录失败有限邮票经理
    //	 *
    //	 * @param authenticationProperties 身份验证属性
    //	 * @return {@link SignInFailureLimitedStampManager }
    //	 * @since 2023-07-10 17:14:20
    //	 */
    //	@Bean
    //	public SignInFailureLimitedStampManager signInFailureLimitedStampManager(
    //		OAuth2AuthenticationProperties authenticationProperties) {
    //		SignInFailureLimitedStampManager manager =
    //			new SignInFailureLimitedStampManager(redisRepository, authenticationProperties);
    //		LogUtils.info("Bean [SignIn Failure Limited Stamp Manager] Auto Configure.");
    //		return manager;
    //	}

    /**
     * auth2表单登录参数配置器
     *
     * @param authenticationProperties 身份验证属性
     * @return {@link OAuth2FormLoginUrlConfigurer }
     * @since 2023-07-10 17:14:21
     */
    //	@Bean
    //	public OAuth2FormLoginUrlConfigurer auth2FormLoginParameterConfigurer(
    //		OAuth2AuthenticationProperties authenticationProperties) {
    //		OAuth2FormLoginUrlConfigurer configurer = new OAuth2FormLoginUrlConfigurer(
    //			authenticationProperties);
    //		log.info("Bean [OAuth2 FormLogin Parameter Configurer] Auto Configure.");
    //		return configurer;
    //	}
}
