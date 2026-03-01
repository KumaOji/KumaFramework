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

package com.kuma.boot.ratelimit.ratelimitguava;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 番石榴限制 直接将注解打在控制层的方法上，需要配合Spring框架的Controller注解使用
 *
 * <pre>{@code
 *
 *   @GuavaLimit(token  = 20, message = "无法访问！")
 *       public List list(@RequestParam final Integer type) {
 *           final KoneOrderType orderType = KoneOrderType.builder().type(type).build();
 *           return mOrderTypeMapper.list(orderType);
 *       }
 *
 *     }
 * </pre>
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-08-08 10:36:16
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GuavaLimit {

    /** 每秒访问的次数 默认可以访问20次 */
    double token() default 20;

    /** 被限流拦截返回客户端的消息 */
    String message() default "无法访问！";
}
