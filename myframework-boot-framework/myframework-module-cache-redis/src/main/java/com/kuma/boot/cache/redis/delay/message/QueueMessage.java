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

package com.kuma.boot.cache.redis.delay.message;

import org.springframework.util.Assert;

import java.util.Map;

/**
 * QueueMessage
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-02-18 10:36:41
 */
public class QueueMessage<T> {

    private final T payload;

    private final Map<String, Object> headers;

    public QueueMessage(T payload, Map<String, Object> headers) {
        Assert.notNull(payload, "payload must not be null");
        this.payload = payload;
        this.headers = headers;
    }

    public T getPayload() {
        return payload;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }
}
