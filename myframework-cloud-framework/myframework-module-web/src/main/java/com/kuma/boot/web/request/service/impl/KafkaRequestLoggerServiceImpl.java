/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  com.kuma.boot.common.utils.json.JacksonUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.kafka.core.KafkaTemplate
 */
package com.kuma.boot.web.request.service.impl;

import com.google.common.base.Stopwatch;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.web.request.model.RequestLog;
import com.kuma.boot.web.request.service.RequestLoggerService;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaRequestLoggerServiceImpl
implements RequestLoggerService {
    private final Stopwatch currentStopwatch = Stopwatch.createStarted();
    private final Stopwatch lastSuccessStopwatch = Stopwatch.createStarted();
    private final Stopwatch lastErrorStopwatch = Stopwatch.createStarted();
    private final AtomicLong sendSuccessNum = new AtomicLong(0L);
    private final AtomicLong sendErrorsNum = new AtomicLong(0L);
    private static final int THRESHOLD = 1000;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaRequestLoggerServiceImpl(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void save(RequestLog requestLog) {
        if (Objects.nonNull(this.kafkaTemplate)) {
            String data = JacksonUtils.toJSONString((Object)requestLog);
            CompletableFuture future = this.kafkaTemplate.send("request-log", (Object)data);
            future.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    long errorNum = this.sendErrorsNum.getAndIncrement();
                    if (errorNum > 0L && errorNum % 1000L == 0L) {
                        this.errorLog(errorNum);
                    }
                } else {
                    long andIncrement = this.sendSuccessNum.getAndIncrement();
                    if (andIncrement > 0L && andIncrement % 1000L == 0L) {
                        this.successLog(andIncrement);
                    }
                }
            });
        }
    }

    protected void successLog(long num) {
        long hour = this.currentStopwatch.elapsed(TimeUnit.HOURS);
        long minute = this.currentStopwatch.elapsed(TimeUnit.MINUTES);
        long seconds = this.currentStopwatch.elapsed(TimeUnit.SECONDS);
        long lastSeconds = this.lastSuccessStopwatch.elapsed(TimeUnit.SECONDS);
        long lastMinute = this.lastSuccessStopwatch.elapsed(TimeUnit.MINUTES);
        long lastHour = this.lastSuccessStopwatch.elapsed(TimeUnit.HOURS);
        LogUtils.info((String)"KafkaRequestLogger [{}\u5df2\u8fbe {}\u6761 \u5171\u7528\u65f6{}\u79d2 {}\u5206 {}\u5c0f\u65f6, \u6700\u8fd1\u4e00\u6b21\u7528\u65f6{}\u79d2 {}\u5206 {}\u5c0f\u65f6]", (Object[])new Object[]{"\u8bf7\u6c42\u65e5\u5fd7\u6d88\u606f\u53d1\u9001\u6210\u529f", num, seconds, minute, hour, lastSeconds, lastMinute, lastHour});
        this.lastSuccessStopwatch.reset().start();
    }

    protected void errorLog(long num) {
        long hour = this.currentStopwatch.elapsed(TimeUnit.HOURS);
        long minute = this.currentStopwatch.elapsed(TimeUnit.MINUTES);
        long seconds = this.currentStopwatch.elapsed(TimeUnit.SECONDS);
        long lastSeconds = this.lastErrorStopwatch.elapsed(TimeUnit.SECONDS);
        long lastMinute = this.lastErrorStopwatch.elapsed(TimeUnit.MINUTES);
        long lastHour = this.lastErrorStopwatch.elapsed(TimeUnit.HOURS);
        LogUtils.error((String)"KafkaRequestLogger [{}\u5df2\u8fbe {}\u6761 \u5171\u7528\u65f6{}\u79d2 {}\u5206 {}\u5c0f\u65f6, \u6700\u8fd1\u4e00\u6b21\u7528\u65f6{}\u79d2 {}\u5206 {}\u5c0f\u65f6]", (Object[])new Object[]{"\u8bf7\u6c42\u65e5\u5fd7\u53d1\u9001\u8fdc\u7a0b\u8bb0\u5f55\u5931\u8d25", num, seconds, minute, hour, lastSeconds, lastMinute, lastHour});
        this.lastErrorStopwatch.reset().start();
    }
}

