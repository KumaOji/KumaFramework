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

package com.kuma.boot.web.request.service.impl;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.constant.RedisConstants;
import com.kuma.boot.common.model.DatePattern;
import com.kuma.boot.common.utils.date.DateUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.request.model.RequestLog;
import com.kuma.boot.web.request.service.RequestLoggerService;

import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 审计日志实现类-redis
 *
 * @author kuma
 * @version 2022.03
 * @since 2020/5/2 11:18
 */
public class RedisRequestLoggerServiceImpl implements RequestLoggerService {

    private final RedisRepository redisRepository;
    private static final int THRESHOLD = 1000;

    private final AtomicLong sendSuccessNum = new AtomicLong(0L);
    private final AtomicLong sendErrorsNum = new AtomicLong(0L);

    public RedisRequestLoggerServiceImpl(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    @Override
    public void save( RequestLog requestLog) {
        String date = DateUtils.format(LocalDate.now(), DatePattern.COLON_DATE_PATTERN);

        if (Objects.nonNull(redisRepository)) {
            redisRepository.send(RedisConstants.REQUEST_LOG_TOPIC, requestLog);

            Long index = redisRepository.leftPush(RedisConstants.REQUEST_LOG + date, requestLog);
            if (index > 0) {
                long andIncrement = sendSuccessNum.getAndIncrement();
                if (andIncrement > 0 && andIncrement % THRESHOLD == 0) {
                    LogUtils.info("RedisRequestLogger 远程日志记录成功：成功条数：{}", andIncrement);
                }
            } else {
                long andIncrement = sendErrorsNum.getAndIncrement();
                if (andIncrement > 0 && andIncrement % THRESHOLD == 0) {
                    LogUtils.error("RedisRequestLogger 远程日志记录失败：失败条数：{}", andIncrement);
                }
            }
        }
    }
}
