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

package com.kuma.boot.security.spring.event;

import com.kuma.boot.common.utils.json.JacksonUtils;

/**
 * <p>策略 Event 定义
 * <p>
 * 为了同时支持 分布式模式 和 单体式模式。所以很多事件均需要同时支持本地发送和远程发送两种模式。
 * 抽象本定义用于统一处理两种模式的事件支持。
 *
 * @author : gengwei.zheng
 * @since : 2022/2/5 15:32
 */
public interface StrategyEvent<T> {

    /**
     * 创建本地事件
     *
     * @param data 事件携带数据
     */
    void postLocalProcess(T data);

    /**
     * 创建远程事件
     *
     * @param data               事件携带数据。JSON 格式的数据。
     * @param originService      发送远程事件原始服务
     * @param destinationService 接收远程事件目的地
     */
    void postRemoteProcess(String data, String originService, String destinationService);

    /**
     * 是否是本地处理事件。
     *
     * @param destinationService 接收远程事件目的地
     * @return false 远程事件，local 本地事件
     */
    boolean isLocal(String destinationService);

    /**
     * 发送事件
     *
     * @param data               事件携带数据
     * @param originService      发送远程事件原始服务
     * @param destinationService 接收远程事件目的地
     */
    default void postProcess(String originService, String destinationService, T data) {
        if (isLocal(destinationService)) {
            postLocalProcess(data);
        } else {
            postRemoteProcess(JacksonUtils.toJson(data), originService, destinationService);
        }
    }
}
