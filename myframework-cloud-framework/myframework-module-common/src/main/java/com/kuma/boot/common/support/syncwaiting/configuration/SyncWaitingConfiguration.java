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

package com.kuma.boot.common.support.syncwaiting.configuration;

/**
 * 同步等待配置。
 */
public class SyncWaitingConfiguration {

    /**
     * 等待时间
     */
    private long waitingTime;

    /**
     * 排队时间
     */
    private long queuingTime;

    public long getWaitingTime() {
        return waitingTime;
    }

    public long getQueuingTime() {
        return queuingTime;
    }

    /**
     * Builder
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-17 10:30:45
     */
    public static class Builder {

        private long waitingTime = 0L;

        private long queuingTime = 0L;

        public Builder waitingTime( long waitingTime ) {
            this.waitingTime = waitingTime;
            return this;
        }

        public Builder queuingTime( long queuingTime ) {
            this.queuingTime = queuingTime;
            return this;
        }

        public SyncWaitingConfiguration build() {
            SyncWaitingConfiguration configuration = new SyncWaitingConfiguration();
            configuration.waitingTime = waitingTime;
            configuration.queuingTime = queuingTime;
            return configuration;
        }
    }
}
