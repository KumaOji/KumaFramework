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

package com.kuma.boot.ratelimit.ratelimitbs.spring.config;

import com.kuma.boot.ratelimit.ratelimitbs.api.core.IRateLimit;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitConfigService;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitMethodService;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitRejectListener;
import com.kuma.boot.ratelimit.ratelimitbs.api.support.IRateLimitTokenService;
import com.kuma.boot.ratelimit.ratelimitbs.core.bs.RateLimitBs;
import com.kuma.boot.ratelimit.ratelimitbs.core.core.RateLimits;
import com.kuma.boot.ratelimit.ratelimitbs.core.support.config.RateLimitConfigService;
import com.kuma.boot.ratelimit.ratelimitbs.core.support.method.RateLimitMethodService;
import com.kuma.boot.ratelimit.ratelimitbs.core.support.reject.RateLimitRejectListenerException;
import com.kuma.boot.ratelimit.ratelimitbs.core.support.token.RateLimitTokenService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.cache.CommonCacheServiceMap;
import com.kuma.boot.ratelimit.ratelimitbs.extend.cache.ICommonCacheService;
import com.kuma.boot.ratelimit.ratelimitbs.extend.timer.ITimer;
import com.kuma.boot.ratelimit.ratelimitbs.extend.timer.Timers;
import com.kuma.boot.ratelimit.ratelimitbs.spring.annotation.EnableRateLimit;
import com.kuma.boot.ratelimit.ratelimitbs.spring.aop.RateLimitAspect;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 限流配置
 * <p>
 * https://blog.csdn.net/u013905744/article/details/91364736
 */
@Configuration
@Import(RateLimitAspect.class)
// @ComponentScan(basePackages = "com.github.houbb.rate.limit.spring")
public class RateLimitConfig implements ImportAware, BeanFactoryPostProcessor {

    /**
     * 属性信息
     */
    private AnnotationAttributes enableAttributes;

    /**
     * bean 工厂
     */
    private ConfigurableListableBeanFactory beanFactory;

    @Bean("rateLimitBs")
    public RateLimitBs rateLimitBs() {
        IRateLimit rateLimit = beanFactory.getBean(enableAttributes.getString("value"), IRateLimit.class);
        ITimer timer = beanFactory.getBean(enableAttributes.getString("timer"), ITimer.class);
        ICommonCacheService cacheService =
                beanFactory.getBean(enableAttributes.getString("cacheService"), ICommonCacheService.class);
        IRateLimitConfigService configService =
                beanFactory.getBean(enableAttributes.getString("configService"), IRateLimitConfigService.class);
        IRateLimitTokenService tokenService =
                beanFactory.getBean(enableAttributes.getString("tokenService"), IRateLimitTokenService.class);
        IRateLimitMethodService methodService =
                beanFactory.getBean(enableAttributes.getString("methodService"), IRateLimitMethodService.class);
        IRateLimitRejectListener rejectListener =
                beanFactory.getBean(enableAttributes.getString("rejectListener"), IRateLimitRejectListener.class);
        String cacheKeyNamespace = enableAttributes.getString("cacheKeyNamespace");

        return RateLimitBs.newInstance()
                .rateLimit(rateLimit)
                .timer(timer)
                .cacheService(cacheService)
                .configService(configService)
                .tokenService(tokenService)
                .methodService(methodService)
                .rejectListener(rejectListener)
                .cacheKeyNamespace(cacheKeyNamespace);
    }

    @Bean("rateLimit")
    public IRateLimit rateLimit() {
        return RateLimits.tokenBucket();
    }

    @Bean("rateLimitTimer")
    public ITimer rateLimitTimer() {
        return Timers.system();
    }

    @Bean("rateLimitCacheService")
    public ICommonCacheService rateLimitCacheService() {
        return new CommonCacheServiceMap();
    }

    @Bean("rateLimitConfigService")
    public IRateLimitConfigService rateLimitConfigService() {
        return new RateLimitConfigService();
    }

    @Bean("rateLimitTokenService")
    public IRateLimitTokenService rateLimitTokenService() {
        return new RateLimitTokenService();
    }

    @Bean("rateLimitMethodService")
    public IRateLimitMethodService rateLimitMethodService() {
        return new RateLimitMethodService();
    }

    @Bean("rateLimitRejectListener")
    public IRateLimitRejectListener rateLimitRejectListener() {
        return new RateLimitRejectListenerException();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setImportMetadata(AnnotationMetadata annotationMetadata) {
        enableAttributes = AnnotationAttributes.fromMap(
                annotationMetadata.getAnnotationAttributes(EnableRateLimit.class.getName(), false));
        if (enableAttributes == null) {
            throw new IllegalArgumentException(
                    "@EnableRateLimit is not present on importing class " + annotationMetadata.getClassName());
        }
    }
}
