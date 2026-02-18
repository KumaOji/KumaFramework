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

package com.kuma.boot.common.support.executor;

import java.io.Serializable;

/**
 * CallableWithParams 是一个泛型接口，允许执行带有多个参数的任务并返回一个结果。
 *
 * @param <R> 返回值的类型
 * @param <P> 参数的类型
 * @Author derek_smart
 * @since 2025/2/18 8:45
 */
@FunctionalInterface
public interface CallableWithParams<R, P> extends Serializable {

    /**
     * 执行任务
     * @param params 参数
     * @return 任务的结果
     * @throws Exception 自定义异常
     */
    R call(P... params) throws Exception;

    /**
     * 执行任务，异常包装为RuntimeException
     * @param params 参数
     * @return 任务的结果
     */
    default R callWithRuntimeException(P... params) {
        try {
            return call(params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行任务，不返回异常
     * @param params 参数
     * @return 任务的结果
     */
    default R callWithoutException(P... params) {
        try {
            return call(params);
        } catch (Exception e) {
            // call quietly
            return null;
        }
    }

    /**
     * 空方法
     * @return 一个空的 CallableWithParams 实例
     */
    static <R, P> CallableWithParams<R, P> empty() {
        return params -> null;
    }
}
