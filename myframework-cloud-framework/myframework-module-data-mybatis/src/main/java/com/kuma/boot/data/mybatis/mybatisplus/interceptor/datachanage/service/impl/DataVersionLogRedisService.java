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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.service.impl;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.constant.RedisConstants;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.model.DataVersionLogData;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.service.DataVersionLogService;

/**
 * 数据版本日志数据库实现
 */
public class DataVersionLogRedisService implements DataVersionLogService {
    private final RedisRepository redisRepository;

    public DataVersionLogRedisService(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    /**
     * 添加
     */
    @Override
    public void add(DataVersionLogData dataVersionLogData) {
        redisRepository.send(RedisConstants.DATA_VERSION_LOG_TOPIC, dataVersionLogData);
    }
}
