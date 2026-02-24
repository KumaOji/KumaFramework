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

package com.kuma.boot.security.spring.constants;

/**
 * <p>Rest 模块常量
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:08:36
 */
public interface RestConstants extends com.kuma.boot.security.spring.constants.BaseConstants {

    /**
     * 财产假装okhttp
     */
    String PROPERTY_FEIGN_OKHTTP = PROPERTY_PREFIX_FEIGN + ".okhttp";

    /**
     * 财产假装httpclient
     */
    String PROPERTY_FEIGN_HTTPCLIENT = PROPERTY_PREFIX_FEIGN + ".httpclient";

    /**
     * 房地产其他扫描
     */
    String PROPERTY_REST_SCAN = PROPERTY_PREFIX_REST + ".scan";

    /**
     * 项扫描启用
     */
    String ITEM_SCAN_ENABLED = PROPERTY_REST_SCAN + PROPERTY_ENABLED;

    /**
     * 项假装okhttp启用
     */
    String ITEM_FEIGN_OKHTTP_ENABLED = PROPERTY_FEIGN_OKHTTP + PROPERTY_ENABLED;

    /**
     * 项假装httpclient启用
     */
    String ITEM_FEIGN_HTTPCLIENT_ENABLED = PROPERTY_FEIGN_HTTPCLIENT + PROPERTY_ENABLED;

    /**
     * 项目保护加密策略
     */
    String ITEM_PROTECT_CRYPTO_STRATEGY = PROPERTY_PREFIX_CRYPTO + ".crypto-strategy";

    /**
     * 缓存名称牌幂等
     */
    String CACHE_NAME_TOKEN_IDEMPOTENT = CACHE_TOKEN_BASE_PREFIX + "idempotent:";

    /**
     * 令牌访问有限缓存名称
     */
    String CACHE_NAME_TOKEN_ACCESS_LIMITED = CACHE_TOKEN_BASE_PREFIX + "access_limited:";

    /**
     * 缓存名称牌安全关键
     */
    String CACHE_NAME_TOKEN_SECURE_KEY = CACHE_TOKEN_BASE_PREFIX + "secure_key:";
}
