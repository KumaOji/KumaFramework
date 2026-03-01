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

package com.kuma.boot.ratelimit.ratelimitbs.api.core;

/**
 * 限流核心接口
 * 后续可以添加 tryAcquire 等方法，指定等待的时间等。
 */
public interface IRateLimit {

    /**
     * 尝试获取指定时间的锁
     * @param context 上下文
     * @return 是否获取到锁
     */
    boolean tryAcquire(final IRateLimitContext context);
}
