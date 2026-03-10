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

package com.kuma.cloud.mq.common.balance.impl;

import com.kuma.cloud.mq.common.balance.LoadBalance;
import com.kuma.cloud.mq.common.balance.Server;
import com.kuma.boot.common.support.hash.api.HashCode;

/**
 * LoadBalances
 *
 * @author kuma
 * @version 2026.02
 * @since 2025-12-19 09:30:45
 */
public final class LoadBalances {

    private LoadBalances() {
    }

    public static <T extends Server> LoadBalance<T> random() {
        return new LoadBalanceRandom();
    }

    public static <T extends Server> LoadBalance<T> roundRobbin() {
        return new LoadBalanceRoundRobbin();
    }

    public static <T extends Server> LoadBalance<T> weightRoundRobbin() {
        return new LoadBalanceWeightRoundRobbin();
    }

    public static <T extends Server> LoadBalance<T> commonHash( HashCode hashCode ) {
        return new LoadBalanceCommonHash(hashCode);
    }

    public static <T extends Server> LoadBalance<T> consistentHash( HashCode hashCode ) {
        return new LoadBalanceConsistentHash(hashCode);
    }
}
