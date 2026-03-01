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

package com.kuma.boot.web.support.methodPartAndRetryer;

import io.github.itning.retry.Retryer;
import io.github.itning.retry.RetryerBuilder;
import io.github.itning.retry.strategy.stop.StopStrategies;
import io.github.itning.retry.strategy.wait.WaitStrategies;

import java.util.concurrent.TimeUnit;

public class RetryUtil<V> {
    /**
     * RetryerBuilder：是用于配置和创建Retryer的构建器。
     * retryIfException：抛出runtime异常、checked异常时都会重试，但是抛出error不会重试。
     * retryIfRuntimeException：只会在抛runtime异常的时候才重试，checked异常和error都不重试。
     * retryIfExceptionOfType：允许我们只在发生特定异常的时候才重试。
     * withWaitStrategy:等待策略，每次请求间隔。
     * withStopStrategy:停止策略，重试多少次后停止。
     * @param times
     * @param waitTime
     * @return
     */
    public Retryer<V> getDefaultRetryer(int times, long waitTime) {
        Retryer<V> retryer =
                RetryerBuilder.<V>newBuilder()
                        .retryIfException()
                        .retryIfRuntimeException()
                        .retryIfExceptionOfType(Exception.class)
                        .withWaitStrategy(WaitStrategies.fixedWait(waitTime, TimeUnit.MILLISECONDS))
                        .withStopStrategy(StopStrategies.stopAfterAttempt(times))
                        .build();
        return retryer;
    }
}
