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

public interface IRateLimitRejectListener {

    /**
     * 监听拒绝的信息
     *
     * 失败时，默认抛出异常。
     *
     * 用户可以结果业务添加对应的报警+黑名单拉黑等操作。
     * @param context 上下文
     */
    void listen(final IRateLimitRejectListenerContext context);
}
