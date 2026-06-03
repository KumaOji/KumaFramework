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

package com.kuma.boot.security.satoken.configuration;

import cn.dev33.satoken.stp.StpUtil;
import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.security.satoken.exception.SaTokenExceptionHandler;
import com.kuma.boot.security.satoken.interceptor.SaTokenRouteInterceptor;
import com.kuma.boot.security.satoken.properties.SaTokenProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 鉴权自动配置.
 *
 * <p>仅在 Servlet Web 环境且引入了 Sa-Token 依赖时激活。自动完成：
 * <ol>
 *   <li>注册 {@link SaTokenRouteInterceptor}，对全路由执行登录校验，白名单路径可通过
 *       {@code kuma.boot.security.sa-token.exclude-urls} 配置</li>
 *   <li>注册 {@link SaTokenExceptionHandler}，将 Sa-Token 鉴权异常统一转换为框架 Result 响应</li>
 *   <li>打印 starter 启动日志</li>
 * </ol>
 *
 * <p><b>业务接入说明</b>
 * <ul>
 *   <li>实现 {@code cn.dev33.satoken.stp.StpInterface} 并注册为 Bean，提供权限/角色列表</li>
 *   <li>使用 {@code StpUtil.login(id)} 登录，{@code StpUtil.logout()} 登出</li>
 *   <li>使用 {@code @SaCheckLogin} / {@code @SaCheckPermission} / {@code @SaCheckRole} 做方法级鉴权</li>
 *   <li>使用框架 {@code @NotAuth} 或 Sa-Token 原生 {@code @SaIgnore} 跳过路由校验</li>
 * </ul>
 *
 * @author kuma
 */
@AutoConfiguration
@EnableConfigurationProperties(SaTokenProperties.class)
@ConditionalOnClass(StpUtil.class)
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnProperty(
        prefix = SaTokenProperties.PREFIX,
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class SaTokenAutoConfiguration implements InitializingBean {

    @Bean
    @ConditionalOnMissingBean
    public SaTokenRouteInterceptor saTokenRouteInterceptor() {
        return new SaTokenRouteInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public SaTokenExceptionHandler saTokenExceptionHandler() {
        return new SaTokenExceptionHandler();
    }

    @Override
    public void afterPropertiesSet() {
        LogUtils.started(SaTokenAutoConfiguration.class, StarterNameConstants.SECURITY_SATOKEN_STARTER);
    }

    /**
     * 注册路由拦截器到 Spring MVC.
     *
     * <p>独立的 {@code @Configuration} 类保证 {@link SaTokenRouteInterceptor} bean
     * 在注册到拦截器链时已由 Spring 容器完整初始化。
     */
    @Configuration(proxyBeanMethods = false)
    static class SaTokenWebMvcConfiguration implements WebMvcConfigurer {

        private final SaTokenRouteInterceptor interceptor;
        private final SaTokenProperties properties;

        SaTokenWebMvcConfiguration(SaTokenRouteInterceptor interceptor, SaTokenProperties properties) {
            this.interceptor = interceptor;
            this.properties = properties;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(interceptor)
                    .addPathPatterns("/**")
                    .excludePathPatterns(properties.getExcludeUrls());
        }
    }
}
