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
 * <p>基础共用常量值常量
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-05 10:16:07
 */
public interface BaseConstants {

    String NONE = "none";
    String CODE = "code";

    /* ---------- 配置属性通用常量 ---------- */

    String PROPERTY_ENABLED = ".enabled";
    String PROPERTY_PREFIX_SPRING = "spring";
    String PROPERTY_PREFIX_FEIGN = "feign";
    String PROPERTY_PREFIX_KMC = "kmc";

    String PROPERTY_SPRING_CLOUD = PROPERTY_PREFIX_SPRING + ".cloud";
    String PROPERTY_SPRING_JPA = PROPERTY_PREFIX_SPRING + ".jpa";
    String PROPERTY_SPRING_REDIS = PROPERTY_PREFIX_SPRING + ".redis";

    String ANNOTATION_PREFIX = "${";
    String ANNOTATION_SUFFIX = "}";

    /* ---------- kmc 自定义配置属性 ---------- */
    String PROPERTY_PREFIX_ACCESS = PROPERTY_PREFIX_KMC + ".access";
    String PROPERTY_PREFIX_CACHE = PROPERTY_PREFIX_KMC + ".cache";
    String PROPERTY_PREFIX_CAPTCHA = PROPERTY_PREFIX_KMC + ".captcha";
    String PROPERTY_PREFIX_CLIENT = PROPERTY_PREFIX_KMC + ".client";
    String PROPERTY_PREFIX_CRYPTO = PROPERTY_PREFIX_KMC + ".crypto";
    String PROPERTY_PREFIX_ENDPOINT = PROPERTY_PREFIX_KMC + ".endpoint";
    String PROPERTY_PREFIX_EVENT = PROPERTY_PREFIX_KMC + ".event";
    String PROPERTY_PREFIX_LOG_CENTER = PROPERTY_PREFIX_KMC + ".log-center";
    String PROPERTY_PREFIX_MANAGE = PROPERTY_PREFIX_KMC + ".manage";
    String PROPERTY_PREFIX_MESSAGE = PROPERTY_PREFIX_KMC + ".message";
    String PROPERTY_PREFIX_MULTI_TENANT = PROPERTY_PREFIX_KMC + ".multi-tenant";
    String PROPERTY_PREFIX_NOSQL = PROPERTY_PREFIX_KMC + ".nosql";
    String PROPERTY_PREFIX_OAUTH2 = PROPERTY_PREFIX_KMC + ".oauth2";
    String PROPERTY_PREFIX_OSS = PROPERTY_PREFIX_KMC + ".oss";
    String PROPERTY_PREFIX_PAY = PROPERTY_PREFIX_KMC + ".pay";
    String PROPERTY_PREFIX_PLATFORM = PROPERTY_PREFIX_KMC + ".platform";
    String PROPERTY_PREFIX_REST = PROPERTY_PREFIX_KMC + ".rest";
    String PROPERTY_PREFIX_SECURE = PROPERTY_PREFIX_KMC + ".secure";
    String PROPERTY_PREFIX_SMS = PROPERTY_PREFIX_KMC + ".sms";
    String PROPERTY_PREFIX_SWAGGER = PROPERTY_PREFIX_KMC + ".swagger";

    /* ---------- Spring 家族配置属性 ---------- */

    String ITEM_SWAGGER_ENABLED = PROPERTY_PREFIX_SWAGGER + PROPERTY_ENABLED;
    String ITEM_SPRING_APPLICATION_NAME = PROPERTY_PREFIX_SPRING + ".application.name";

    String ANNOTATION_APPLICATION_NAME =
            ANNOTATION_PREFIX + ITEM_SPRING_APPLICATION_NAME + ANNOTATION_SUFFIX;

    /* ---------- 通用缓存常量 ---------- */

    String CACHE_PREFIX = "cache:";
    String CACHE_SIMPLE_BASE_PREFIX = CACHE_PREFIX + "simple:";
    String CACHE_TOKEN_BASE_PREFIX = CACHE_PREFIX + "token:";

    String AREA_PREFIX = "data:";

    /* ---------- Oauth2 和 Security 通用缓存常量 ---------- */

    /**
     * Oauth2 模式类型
     */
    String PASSWORD = "password";

    String SOCIAL_CREDENTIALS = "social_credentials";

    String OPEN_API_SECURITY_SCHEME_BEARER_NAME = "KMC_AUTH";

    String BEARER_TYPE = "Bearer";
    String BEARER_TOKEN = BEARER_TYPE + SymbolConstants.SPACE;
    String BASIC_TYPE = "Basic";
    String BASIC_TOKEN = BASIC_TYPE + SymbolConstants.SPACE;

    String AUTHORITIES = "authorities";
    String AVATAR = "avatar";
    String EMPLOYEE_ID = "employeeId";
    String LICENSE = "license";
    String OPEN_ID = "openid";
    String PRINCIPAL = "principal";
    String ROLES = "roles";
    String SOURCE = "source";
    String USERNAME = "username";
}
