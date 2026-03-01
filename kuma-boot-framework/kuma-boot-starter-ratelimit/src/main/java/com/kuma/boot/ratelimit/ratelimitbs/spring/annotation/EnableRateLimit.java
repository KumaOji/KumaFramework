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

package com.kuma.boot.ratelimit.ratelimitbs.spring.annotation;

import com.kuma.boot.ratelimit.ratelimitbs.core.constant.RateLimitConst;
import com.kuma.boot.ratelimit.ratelimitbs.spring.config.RateLimitConfig;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用自动限制注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RateLimitConfig.class)
@EnableAspectJAutoProxy
public @interface EnableRateLimit {

    /**
     * 限流策略
     * @return 限流策略
     */
    String value() default "rateLimit";

    /**
     * 时间策略
     * @return 时间策略
     */
    String timer() default "rateLimitTimer";

    /**
     * 缓存策略
     * @return 缓存策略
     */
    String cacheService() default "rateLimitCacheService";

    /**
     * 配置策略
     * @return 配置策略
     */
    String configService() default "rateLimitConfigService";

    /**
     * 标识服务类
     * @return 标识
     */
    String tokenService() default "rateLimitTokenService";

    /**
     * 方法服务类
     * @return 服务类
     */
    String methodService() default "rateLimitMethodService";

    /**
     * 拒绝策略
     * @return 策略
     */
    String rejectListener() default "rateLimitRejectListener";

    /**
     * 缓存的命名空间
     * @since 1.1.0
     * @return 结果
     */
    String cacheKeyNamespace() default RateLimitConst.DEFAULT_CACHE_KEY_NAMESPACE;
}
