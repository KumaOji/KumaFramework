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

package com.kuma.cloud.gateway.support;

import java.util.List;

/**
 * 网关与下游服务之间的约定请求头。
 *
 * <p>{@link #IDENTITY_HEADERS} 中的头只允许网关写入：入口处会无条件剔除客户端自带的同名头，
 * 避免外部请求伪造身份直接越过鉴权。
 *
 * @author kuma
 * @since 2026-04-23
 */
public final class GatewayHeaders {

    /** 标记请求来自网关，下游可据此拒绝绕过网关的直连流量。 */
    public static final String GATEWAY_SOURCE = "X-Gateway-Source";

    public static final String GATEWAY_SOURCE_VALUE = "kuma-gateway";

    public static final String USER_ID = "X-User-Id";

    public static final String USER_NAME = "X-User-Name";

    public static final String USER_AUTHORITIES = "X-User-Authorities";

    public static final String CLIENT_ID = "X-Client-Id";

    /** 真实客户端 IP，供下游做风控 / 审计。 */
    public static final String CLIENT_IP = "X-Client-Ip";

    public static final String RESPONSE_TIME = "X-Response-Time";

    public static final List<String> IDENTITY_HEADERS =
            List.of(GATEWAY_SOURCE, USER_ID, USER_NAME, USER_AUTHORITIES, CLIENT_ID, CLIENT_IP);

    private GatewayHeaders() {}
}
