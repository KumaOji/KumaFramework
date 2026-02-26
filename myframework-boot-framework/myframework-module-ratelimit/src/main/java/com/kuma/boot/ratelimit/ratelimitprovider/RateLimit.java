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

package com.kuma.boot.ratelimit.ratelimitprovider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 速率限制
 *
 * @author kuma
 * @version 2022.09
 * @since 2022-10-26 08:51:31
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * 时间窗口流量数量
     *
     * @return long
     * @since 2022-10-26 08:51:31
     */
    long rate();

    /**
     * 时间窗口流量数量表达式
     *
     * @return {@link String }
     * @since 2022-10-26 08:51:31
     */
    String rateExpression() default "";

    /**
     * 时间窗口，最小单位秒，如 2s，2h , 2d
     *
     * @return {@link String }
     * @since 2022-10-26 08:51:31
     */
    String rateInterval();

    /**
     * 获取key
     *
     * @return {@link String[] }
     * @since 2022-10-26 08:51:31
     */
    String[] keys() default {};

    /**
     * 自定义业务 key 的 Function
     *
     * @return {@link String }
     * @since 2022-10-26 08:51:31
     */
    String keyFunction() default "";

    /**
     * 限流后的自定义回退后的拒绝逻辑
     *
     * @return {@link String }
     * @since 2022-10-26 08:51:31
     */
    String fallbackFunction() default "";
}
