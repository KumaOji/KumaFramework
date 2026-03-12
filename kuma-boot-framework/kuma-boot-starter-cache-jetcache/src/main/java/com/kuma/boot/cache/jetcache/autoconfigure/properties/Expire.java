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

package com.kuma.boot.cache.jetcache.autoconfigure.properties;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 单个 cache 的过期时间配置，支持 {@link TimeUnit} 所有粒度。
 *
 * @author kuma
 * @since 2022-07-03
 */
public class Expire {

    private Long duration = 1L;
    private TimeUnit unit = TimeUnit.HOURS;

    /** 惰性计算的 {@link Duration}，首次调用 {@link #getTtl()} 时初始化。 */
    private Duration ttl;

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
        this.ttl = null; // 重置，以便重新计算
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
        this.ttl = null; // 重置，以便重新计算
    }

    /** 返回对应的 {@link Duration}，使用 {@link TimeUnit#toChronoUnit()} 精确转换所有时间单位。 */
    public Duration getTtl() {
        if (ttl == null) {
            ttl = Duration.of(duration, unit.toChronoUnit());
        }
        return ttl;
    }
}
