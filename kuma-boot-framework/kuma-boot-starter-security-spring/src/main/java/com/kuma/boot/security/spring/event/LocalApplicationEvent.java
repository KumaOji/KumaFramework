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

package com.kuma.boot.security.spring.event;

import java.time.Clock;
import org.springframework.context.ApplicationEvent;

/**
 * <p>自定义 Application Event 基础类
 *
 * @author : gengwei.zheng
 * @since : 2022/2/4 15:14
 */
public class LocalApplicationEvent<T> extends ApplicationEvent {

    private final T data;

    public LocalApplicationEvent(T data) {
        super(data);
        this.data = data;
    }

    public LocalApplicationEvent(T data, Clock clock) {
        super(data, clock);
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
