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

/**
 * <p>应用策略事件
 * @since 2022/3/29 7:26
 */
public interface ApplicationStrategyEvent<T> extends StrategyEvent<T> {

    @Override
    default boolean isLocal(String destinationService) {
        //		return !ServiceContext.getInstance().isDistributedArchitecture()
        //			|| StringUtils.equals(ServiceContext.getInstance().getApplicationName(),
        // destinationService);
        return true;
    }

    /**
     * 发送事件
     *
     * @param data               事件携带数据
     * @param destinationService 接收远程事件目的地
     */
    default void postProcess(String destinationService, T data) {
        String originService = "http://127.0.0.1:3337";
        //		postProcess(ServiceContext.getInstance().getOriginService(), destinationService, data);
        postProcess(originService, destinationService, data);
    }
}
