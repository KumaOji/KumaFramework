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

package com.kuma.boot.ratelimit.ratelimitbs.core.dto;

import java.io.Serializable;

public class RateLimitTokenBucketDto implements Serializable {

    /**
     * 令牌的发放速率
     * <p>
     * 每一秒发放多少。
     *
     *
     */
    private long rate;

    /**
     * 容量
     * <p>
     * 后期暴露为可以配置
     *
     *
     */
    private long capacity;

    /**
     * 令牌数量
     *
     *
     */
    private volatile long tokenNum;

    /**
     * 上一次的更新时间
     *
     *
     */
    private volatile long lastUpdateTime;

    public long getRate() {
        return rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }

    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public long getTokenNum() {
        return tokenNum;
    }

    public void setTokenNum(long tokenNum) {
        this.tokenNum = tokenNum;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return "RateLimitBucketDto{" + "rate="
                + rate + ", capacity="
                + capacity + ", tokenNum="
                + tokenNum + ", lastUpdateTime="
                + lastUpdateTime + '}';
    }
}
