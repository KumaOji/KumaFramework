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

package com.kuma.cloud.cache.support.listener.remove;

import com.kuma.cloud.cache.api.CacheRemoveListenerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认的删除监听类
 * @author kuma
 * @since 2024.06
 */
public class CacheRemoveListener<K, V> implements com.kuma.cloud.cache.api.CacheRemoveListener<K, V> {

    private static final Logger log = LoggerFactory.getLogger(CacheRemoveListener.class);

    @Override
    public void listen( CacheRemoveListenerContext<K, V> context) {
        log.debug(
                "Remove key: {}, value: {}, type: {}",
                context.key(),
                context.value(),
                context.type());
    }
}
