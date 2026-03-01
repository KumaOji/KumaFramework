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

package com.kuma.boot.web.aop.aop.caching;

import java.lang.annotation.*;

/**
 * Caching 方法级的缓存，默认使用guava-cache本地缓存，可以实现CacheProvider接口并声明@Bean来替换
 *
 * {@snippet :
 * 	@Service
 * public class ItemService {
 *     // 缓存key为item:xxx 缓存时间5分钟 查询结果为空也要缓存防止穿透
 *     @Caching(value = "'item:'.concat(#itemId)", expireMillis = 5 * 60 * 1000, cacheIfNull = true)
 *     public ItemDTO queryItemById(String itemId) {
 *
 *     }
 * }
 * }
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Caching {

    /**
     * 缓存的key表达式
     */
    String value();

    /**
     * 缓存的过期时间(毫秒) 默认为30秒
     */
    long expireMillis() default 30_000;

    /**
     * 是否缓存null值 默认不缓存
     */
    boolean cacheIfNull() default false;
}
