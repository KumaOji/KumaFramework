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

import static cn.hutool.core.date.DatePattern.NORM_DATETIME_PATTERN;

/**
 * <p>默认常量合集
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:09:55
 */
public interface DefaultConstants {

    /**
     * 授权端点
     */
    String AUTHORIZATION_ENDPOINT = "/oauth2/authorize";

    /**
     * 令牌端点
     */
    String TOKEN_ENDPOINT = "/oauth2/token";

    /**
     * 令牌撤销端点
     */
    String TOKEN_REVOCATION_ENDPOINT = "/oauth2/revoke";

    /**
     * 令牌内省端点
     */
    String TOKEN_INTROSPECTION_ENDPOINT = "/oauth2/introspect";

    /**
     * 设备授权端点
     */
    String DEVICE_AUTHORIZATION_ENDPOINT = "/oauth2/device_authorization";

    /**
     * 设备验证端点
     */
    String DEVICE_VERIFICATION_ENDPOINT = "/oauth2/device_verification";

    /**
     * jwk设置端点
     */
    String JWK_SET_ENDPOINT = "/oauth2/jwks";

    /**
     * oidc客户注册端点
     */
    String OIDC_CLIENT_REGISTRATION_ENDPOINT = "/connect/register";

    /**
     * oidc注销端点
     */
    String OIDC_LOGOUT_ENDPOINT = "/connect/logout";

    /**
     * oidc端点用户信息
     */
    String OIDC_USER_INFO_ENDPOINT = "/userinfo";

    /**
     * 授权同意uri
     */
    String AUTHORIZATION_CONSENT_URI = "/oauth2/consent";

    /**
     * 设备激活uri
     */
    String DEVICE_ACTIVATION_URI = "/oauth2/device_activation";

    /**
     * 设备验证成功uri
     */
    String DEVICE_VERIFICATION_SUCCESS_URI = "/device_activated";

    /**
     * 默认租户ID
     */
    String TENANT_ID = "public";

    /**
     * 默认树形结构根节点
     */
    String TREE_ROOT_ID = "0";

    /**
     * 默认的时间日期格式
     */
    String DATE_TIME_FORMAT = NORM_DATETIME_PATTERN;

    /**
     * minio多部分请求映射
     */
    String MINIO_MULTIPART_REQUEST_MAPPING = "/oss/minio/multipart";

    /**
     * minio presigned对象请求映射
     */
    String MINIO_PRESIGNED_OBJECT_REQUEST_MAPPING = "/presigned";

    /**
     * minio presigned对象代理
     */
    String MINIO_PRESIGNED_OBJECT_PROXY = MINIO_PRESIGNED_OBJECT_REQUEST_MAPPING + "/**";

    /**
     * presigned url对象代理
     */
    String PRESIGNED_OBJECT_URL_PROXY =
            MINIO_MULTIPART_REQUEST_MAPPING + MINIO_PRESIGNED_OBJECT_REQUEST_MAPPING;
}
