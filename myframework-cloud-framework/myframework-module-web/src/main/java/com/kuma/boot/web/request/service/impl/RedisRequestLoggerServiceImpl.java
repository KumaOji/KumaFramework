/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.cache.redis.repository.RedisRepository
 *  com.kuma.boot.common.utils.date.DateUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.web.request.service.impl;

import com.kuma.boot.cache.redis.repository.RedisRepository;
import com.kuma.boot.common.utils.date.DateUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.request.model.RequestLog;
import com.kuma.boot.web.request.service.RequestLoggerService;
import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class RedisRequestLoggerServiceImpl
implements RequestLoggerService {
    private final RedisRepository redisRepository;
    private static final int THRESHOLD = 1000;
    private final AtomicLong sendSuccessNum = new AtomicLong(0L);
    private final AtomicLong sendErrorsNum = new AtomicLong(0L);

    public RedisRequestLoggerServiceImpl(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    @Override
    public void save(RequestLog requestLog) {
        String date = DateUtils.format((LocalDate)LocalDate.now(), (String)"yyyy:MM:dd");
        if (Objects.nonNull(this.redisRepository)) {
            this.redisRepository.send("REQUEST:LOG:TOPIC", (Object)requestLog);
            Long index = this.redisRepository.leftPush("REQUEST:LOG:" + date, (Object)requestLog);
            if (index > 0L) {
                long andIncrement = this.sendSuccessNum.getAndIncrement();
                if (andIncrement > 0L && andIncrement % 1000L == 0L) {
                    LogUtils.info((String)"RedisRequestLogger \u8fdc\u7a0b\u65e5\u5fd7\u8bb0\u5f55\u6210\u529f\uff1a\u6210\u529f\u6761\u6570\uff1a{}", (Object[])new Object[]{andIncrement});
                }
            } else {
                long andIncrement = this.sendErrorsNum.getAndIncrement();
                if (andIncrement > 0L && andIncrement % 1000L == 0L) {
                    LogUtils.error((String)"RedisRequestLogger \u8fdc\u7a0b\u65e5\u5fd7\u8bb0\u5f55\u5931\u8d25\uff1a\u5931\u8d25\u6761\u6570\uff1a{}", (Object[])new Object[]{andIncrement});
                }
            }
        }
    }
}

