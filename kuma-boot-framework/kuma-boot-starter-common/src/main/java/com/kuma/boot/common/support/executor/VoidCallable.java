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

package com.kuma.boot.common.support.executor;

import java.io.Serializable;

/**
 * VoidCallable 是一个函数式接口，表示一个无参数且不返回值的任务。 该接口可以抛出异常。
 *
 * @Author derek_smart
 * @since 2025/2/18 8:40
 */
public interface VoidCallable extends Serializable {

    /**
     * 执行任务。
     * @throws Exception 如果任务执行过程中发生异常
     */
    void call() throws Exception;

    /**
     * 执行函数，异常包装为RuntimeException
     */
    default void callWithRuntimeException() {
        try {
            call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行函数，不返回异常
     */
    default void callWithoutException() {
        try {
            call();
        } catch (Exception e) {
            // call quietly
        }
    }

    /**
     * 空方法
     * @return
     */
    static VoidCallable empty() {
        return () -> {};
    }
}
