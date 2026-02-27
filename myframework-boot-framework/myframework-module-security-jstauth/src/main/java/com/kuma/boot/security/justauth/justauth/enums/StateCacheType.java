/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.security.justauth.justauth.enums;

/**
 * state cache type
 * @author YongWu zheng
 * @version V1.0  Created by 2020/10/6 20:09
 */
public enum StateCacheType {
    /**
     * 本地内存, 适用单机
     */
    DEFAULT,
    /**
     * session, 根据 session 的缓存模式是否适用分布式来决定是否适用单机与分布式
     */
    SESSION,
    /**
     * redis, 适用单机与分布式
     */
    REDIS
}
