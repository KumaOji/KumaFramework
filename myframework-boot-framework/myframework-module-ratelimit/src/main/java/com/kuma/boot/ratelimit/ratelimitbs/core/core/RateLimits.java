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

package com.kuma.boot.ratelimit.ratelimitbs.core.core;

import com.kuma.boot.ratelimit.ratelimitbs.api.core.IRateLimit;

public final class RateLimits {

    private RateLimits() {}

    /**
     * 固定窗口
     * @return 策略
     */
    public static IRateLimit fixedWindow() {
        return new com.kuma.boot.ratelimit.ratelimitbs.core.core.RateLimitFixedWindow();
    }

    /**
     * 滑动窗口
     * @return 策略
     */
    public static IRateLimit slideWindow() {
        return new RateLimitSlideWindow();
    }

    /**
     * 滑动窗口
     * @param windowNum 窗口数量
     * @return 策略
     */
    public static IRateLimit slideWindow(int windowNum) {
        return new RateLimitSlideWindow(windowNum);
    }

    /**
     * 滑动窗口队列实现
     * @return 策略
     */
    public static IRateLimit slideWindowQueue() {
        return new RateLimitSlideWindowQueue();
    }

    /**
     * 漏桶算法
     * @return 策略
     */
    public static IRateLimit leakyBucket() {
        return new com.kuma.boot.ratelimit.ratelimitbs.core.core.RateLimitLeakyBucket();
    }

    /**
     * 令牌桶算法
     * @return 策略
     */
    public static IRateLimit tokenBucket() {
        return new RateLimitTokenBucket();
    }
}
