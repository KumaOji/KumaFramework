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

package com.kuma.boot.ratelimit.ratelimitprovider;

/**
 * 速度限制器信息
 *
 * @author kuma
 * @version 2022.09
 * @since 2022-10-26 08:56:46
 */
public class RateLimiterInfo {

    private String key;
    private long rate;
    private long rateInterval;

    public RateLimiterInfo(String key, long rate, long rateInterval) {
        this.key = key;
        this.rate = rate;
        this.rateInterval = rateInterval;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getRate() {
        return rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }

    public long getRateInterval() {
        return rateInterval;
    }

    public void setRateInterval(long rateInterval) {
        this.rateInterval = rateInterval;
    }
}
