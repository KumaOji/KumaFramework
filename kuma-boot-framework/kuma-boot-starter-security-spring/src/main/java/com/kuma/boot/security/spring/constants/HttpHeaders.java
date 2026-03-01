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
 * <p>自定义请求头
 *
 */
public interface HttpHeaders {

    String UNKNOWN = "unknown";
    String PROXY_CLIENT_IP = "Proxy-Client-IP";
    String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
    String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
    String HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";
    String X_REAL_IP = "X-Real-IP";
    String X_KMC_SESSION = "X-Kmc-Session";
    String X_KMC_FROM_IN = "X-Kmc-From-In";
    String X_KMC_TENANT_ID = "X-Kmc-Tenant-Id";
    String X_KMC__OPEN_ID = "X-Kmc-Open-Id";
}
