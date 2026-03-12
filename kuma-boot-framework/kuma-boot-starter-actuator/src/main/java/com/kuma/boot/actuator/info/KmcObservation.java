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

package com.kuma.boot.actuator.info;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

/**
 * Micrometer Observation 使用示例：演示如何对业务方法进行埋点观测。
 *
 * <p>通过 {@link ObservationRegistry} 创建带低/高基数标签的观测，
 * 自动衔接 Micrometer Tracing / Prometheus 等后端。
 *
 * @author kuma
 * @since 2025-12-19
 */
public class KmcObservation {

    private final ObservationRegistry observationRegistry;

    public KmcObservation(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    /**
     * 使用 Observation 对目标逻辑进行埋点，所有 span / metric 自动上报。
     *
     * @param name   观测名称（低基数，用于指标 key）
     * @param locale 低基数标签：locale
     * @param userId 高基数标签：userId（不建议用于 metric 聚合）
     * @param task   被观测的实际逻辑
     */
    public void observe(String name, String locale, String userId, Runnable task) {
        Observation.createNotStarted(name, observationRegistry)
                .lowCardinalityKeyValue("locale", locale)
                .highCardinalityKeyValue("userId", userId)
                .observe(task);
    }
}
