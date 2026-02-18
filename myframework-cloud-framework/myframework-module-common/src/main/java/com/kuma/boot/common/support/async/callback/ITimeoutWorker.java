/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.support.async.callback;

/**
 * @author wuweifeng wrote on 2019-12-20
 * @version 1.0
 */
public interface ITimeoutWorker<T, V> extends IWorker<T, V> {

    /**
     * 每个worker都可以设置超时时间
     * @return 毫秒超时时间
     */
    long timeOut();

    /**
     * 是否开启单个执行单元的超时功能（有时是一个group设置个超时，而不具备关心单个worker的超时）
     * <p>
     * 注意，如果开启了单个执行单元的超时检测，将使线程池数量多出一倍
     * </p>
     * @return 是否开启
     */
    boolean enableTimeOut();
}
