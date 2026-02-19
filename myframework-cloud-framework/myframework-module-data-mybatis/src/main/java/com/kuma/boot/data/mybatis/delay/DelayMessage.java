/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.delay;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/** 延迟消息体 */
public class DelayMessage implements Delayed {

    /** 消息内容 */
    private String message; // 延迟任务中的任务数据

    /** ttl */
    private long ttl; // 延迟任务到期时间（过期时间）

    /**
     * 构造函数
     * @param message 消息实体
     * @param ttl     延迟时间，单位毫秒
     */
    public DelayMessage(String message, long ttl) {
        setMessage(message);
        this.ttl = System.currentTimeMillis() + ttl;
    }

    /**
     * 获取消息触发剩余时间
     * @param unit the time unit
     * @return {@link long}
     */
    @Override
    public long getDelay(TimeUnit unit) {
        // 计算该任务距离过期还剩多少时间
        long remaining = ttl - System.currentTimeMillis();
        return unit.convert(remaining, TimeUnit.MILLISECONDS);
    }

    /**
     * 比较消息延时时长
     * @param o {@link Delayed}
     * @return 延时时长
     */
    @Override
    public int compareTo(Delayed o) {
        // 比较、排序: 对任务的延时大小进行排序，将延时时间最小的任务放到队列头部
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }
}
