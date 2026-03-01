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

package com.kuma.boot.ratelimit.ratelimitbs.api.support;

import com.kuma.boot.ratelimit.ratelimitbs.api.dto.RateLimitConfigDto;

import java.lang.reflect.Method;
import java.util.List;

public interface IRateLimitConfigService {

    /**
     * 获取当前用户，当前方法对应的配置信息
     *
     * ps: 返回配置列表，支持多种不同的限制。
     *
     * 比如 1min 内 10次，1H内 100 次等等。
     *
     * @param tokenId 用户标识
     * @param methodId 方法标识
     * @param method 方法信息
     * @return 结果
     */
    List<RateLimitConfigDto> queryConfigList(String tokenId, String methodId, Method method);
}
