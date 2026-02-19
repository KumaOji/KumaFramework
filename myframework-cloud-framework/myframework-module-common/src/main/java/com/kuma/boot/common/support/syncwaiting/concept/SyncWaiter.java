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

package com.kuma.boot.common.support.syncwaiting.concept;

/**
 * 同步等待器，与每个请求一一对应。
 */
public interface SyncWaiter {

    /**
     * 获得标识。
     * @return 标识
     */
    Object key();

    /**
     * 设置标识。
     * @param key 标识
     */
    void key(Object key);

    /**
     * 获得值。
     * @param <T> 值类型
     * @return 值
     */
    <T> T value();

    /**
     * 设置值。
     * @param value 值
     */
    void value(Object value);

    /**
     * 阻塞等待。
     * @param time 等待超时时间
     */
    void performWait(long time);

    /**
     * 通知唤醒。
     */
    void performNotify();
}
