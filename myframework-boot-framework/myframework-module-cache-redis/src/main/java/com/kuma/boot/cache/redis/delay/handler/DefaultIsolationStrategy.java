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

package com.kuma.boot.cache.redis.delay.handler;

import com.kuma.boot.common.utils.log.LogUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * DefaultIsolationStrategy
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-02-18 10:25:25
 */
public class DefaultIsolationStrategy implements IsolationStrategy {

    @Override
    public String getRedisQueueName(String queue) {
        String prefix;
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String hostAddress = localHost.getHostAddress();
            String hostName = localHost.getHostName();
            prefix = hostName + "@" + hostAddress;
        } catch (UnknownHostException e) {
            LogUtils.warn("can not detect host info,instead with localhost@127.0.0.1");
            prefix = "localhost@127.0.0.1";
        }
        return prefix + "-" + queue;
    }
}
