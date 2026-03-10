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

package com.kuma.cloud.mq.common.retry.core.model;

import com.kuma.cloud.mq.common.retry.api.model.WaitTime;
import java.util.concurrent.TimeUnit;

/**
 * @author kuma
 * @since 0.0.1
 */
public class DefaultWaitTime implements WaitTime {

    /**
     * 等待时间
     */
    private final long time;

    /**
     * 时间单位
     */
    private final TimeUnit unit;

    public DefaultWaitTime(long time) {
        this.time = time;
        this.unit = TimeUnit.MILLISECONDS;
    }

    public DefaultWaitTime(long time, TimeUnit unit) {
        this.time = time;
        this.unit = unit;
    }

    @Override
    public long time() {
        return this.time;
    }

    @Override
    public TimeUnit unit() {
        return this.unit;
    }
}
