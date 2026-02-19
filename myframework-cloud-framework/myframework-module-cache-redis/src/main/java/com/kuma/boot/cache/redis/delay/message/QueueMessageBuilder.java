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

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * QueueMessageBuilder
 *
 * @author kuma
 * @version 2021.10
 * @since 2022-02-18 10:36:41
 */
public class QueueMessageBuilder<T> {

    private T payload;

    private Map<String, Object> headers;

    public static <T> QueueMessageBuilder<T> withPayload(T payload) {
        Assert.notNull(payload, "payload must not be null");
        QueueMessageBuilder<T> builder = new QueueMessageBuilder<>();
        builder.payload = payload;
        return builder;
    }

    public QueueMessageBuilder<T> headers(Map<String, Object> headers) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        this.headers = headers;
        return this;
    }

    public QueueMessage<T> build() {
        return new QueueMessage<>(this.payload, this.headers);
    }
}
