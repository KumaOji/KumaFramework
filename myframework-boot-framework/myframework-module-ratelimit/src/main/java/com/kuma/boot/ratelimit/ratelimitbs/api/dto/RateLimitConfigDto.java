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

package com.kuma.boot.ratelimit.ratelimitbs.api.dto;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class RateLimitConfigDto implements Serializable {

    /**
     * 每次访问消耗的令牌数
     */
    private int permits;

    /**
     * 单位
     */
    private TimeUnit timeUnit;

    /**
     * 时间间隔
     */
    private long interval;

    /**
     * 次数
     */
    private Long count;

    /**
     * 是否启用
     *
     * @since 1.1.0
     */
    private boolean enable;

    public int getPermits() {
        return permits;
    }

    public void setPermits(int permits) {
        this.permits = permits;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String toString() {
        return "RateLimitConfigDto{" + "permits="
                + permits + ", timeUnit="
                + timeUnit + ", interval="
                + interval + ", count="
                + count + ", enable="
                + enable + '}';
    }
}
