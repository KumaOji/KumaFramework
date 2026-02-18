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

import com.kuma.boot.common.support.async.wrapper.WorkerWrapper;
import java.util.Map;

/**
 * 每个最小执行单元需要实现该接口
 *
 * @author wuweifeng wrote on 2019-11-19.
 */
@FunctionalInterface
public interface IWorker<T, V> {

    /**
     * 在这里做耗时操作，如rpc请求、IO等
     * @param object object
     * @param allWrappers 任务包装
     */
    V action(T object, Map<String, WorkerWrapper<?, ?>> allWrappers);

    /**
     * 超时、异常时，返回的默认值
     * @return 默认值
     */
    default V defaultValue() {
        return null;
    }
}
