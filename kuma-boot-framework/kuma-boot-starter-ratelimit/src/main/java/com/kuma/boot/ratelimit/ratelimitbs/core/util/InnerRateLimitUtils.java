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

package com.kuma.boot.ratelimit.ratelimitbs.core.util;

import com.kuma.boot.ratelimit.ratelimitbs.api.dto.RateLimitConfigDto;

public class InnerRateLimitUtils {

    private InnerRateLimitUtils() {}

    /**
     * 计算速率
     * @param configDto 配置
     * @return 结果
     */
    public static Long calcRate(RateLimitConfigDto configDto) {
        // 暂不考虑特殊输入，比如 1s 令牌少于1 的场景
        long intervalSeconds = configDto.getTimeUnit().toSeconds(configDto.getInterval());
        // 速率
        return Math.max(1, configDto.getCount() / intervalSeconds);
    }
}
