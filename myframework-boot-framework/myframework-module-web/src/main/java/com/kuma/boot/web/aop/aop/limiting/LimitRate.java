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

package com.kuma.boot.web.aop.aop.limiting;

import java.lang.annotation.*;

/**
 * 需要限速
 *
 * {@snippet :
 * 	@Service
 * public class ItemService {
 *     // 限流key为queryItemById，QPS不能超过200，否则抛出RateLimitedException
 *     @LimitRate(spel = "queryItemById", qps = 200)
 *     public ItemDTO query(String itemId) {
 *
 *     }
 *
 *     // 限流key为queryItemById 与query方法共享限流，这两个方法总QPS不能超200
 *     @LimitRate(spel = "queryItemById", qps = 200)
 *     public ItemDTO queryList(List<String> itemIds) {
 *
 *     }
 * }
 * }
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LimitRate {

    /**
     * 限速的key表达式 多个方法可共用同一个key来共享限制
     */
    String spel();

    /**
     * 方法的QPS 默认为200(经验值)
     */
    int qps() default 200;
}
