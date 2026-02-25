/*
 *  com.alibaba.fastjson2.JSON
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.ratelimit.ratelimitsnowjean.core.limiter;

import com.alibaba.fastjson2.JSON;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.ratelimit.ratelimitsnowjean.commoon.entity.RateLimiterRule;
import com.kuma.boot.ratelimit.ratelimitsnowjean.commoon.enums.LimiterModel;
import com.kuma.boot.ratelimit.ratelimitsnowjean.commoon.enums.RuleAuthority;
import com.kuma.boot.ratelimit.ratelimitsnowjean.core.config.RateLimiterConfig;
import com.kuma.boot.ratelimit.ratelimitsnowjean.core.monitor.MonitorServiceImpl;
import com.kuma.boot.ratelimit.ratelimitsnowjean.monitor.client.MonitorService;
import com.kuma.boot.ratelimit.ratelimitsnowjean.monitor.entity.MonitorBean;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class RateLimiterDefault
implements RateLimiter {
    private final AtomicLong bucket = new AtomicLong(0L);
    private RateLimiterRule rule;
    private RateLimiterConfig config;
    private ScheduledFuture<?> scheduledFuture;
    private MonitorService monitorService = new MonitorServiceImpl();

    @Override
    public MonitorService getMonitorService() {
        return this.monitorService;
    }

    RateLimiterDefault(RateLimiterRule rule, RateLimiterConfig config) {
        this.config = config;
        this.init(rule);
    }

    @Override
    public void init(RateLimiterRule rule) {
        this.rule = rule;
        this.putPointBucket();
    }

    @Override
    public boolean tryAcquire(String o) {
        return (switch (this.rule.getRuleAuthority()) {
            case RuleAuthority.AUTHORITY_BLACK -> Stream.of(this.rule.getLimitUser()).noneMatch(s -> s.equals(o));
            case RuleAuthority.AUTHORITY_WHITE -> Arrays.asList(this.rule.getLimitUser()).contains(o);
            default -> true;
        }) && this.tryAcquire();
    }

    @Override
    public boolean tryAcquire() {
        if (this.rule.isEnable()) {
            return true;
        }
        return this.tryAcquireMonitor();
    }

    private boolean tryAcquireMonitor() {
        if (this.rule.getLimiterModel() == LimiterModel.POINT) {
            return this.tryAcquirePut();
        }
        MonitorBean monitor = new MonitorBean();
        monitor.setLocalDateTime(LocalDateTime.now());
        monitor.setPre(1);
        monitor.setApp(this.rule.getApp());
        monitor.setId(this.rule.getId());
        monitor.setName(this.rule.getName());
        monitor.setMonitor(this.rule.getMonitor());
        boolean b = this.tryAcquirePut();
        if (b) {
            monitor.setAfter(1);
        }
        this.config.getScheduledThreadExecutor().execute(() -> this.monitorService.save(monitor));
        return b;
    }

    private boolean tryAcquirePut() {
        boolean result = this.tryAcquireFact();
        this.putCloudBucket();
        return result;
    }

    private boolean tryAcquireFact() {
        if (this.rule.getLimit() == 0L) {
            return false;
        }
        boolean result = false;
        switch (this.rule.getAcquireModel()) {
            case FAILFAST: {
                result = this.tryAcquireFailed();
                break;
            }
            case BLOCKING: {
                result = this.tryAcquireSucceed();
            }
        }
        return result;
    }

    private boolean tryAcquireFailed() {
        long l = this.bucket.longValue();
        while (l > 0L) {
            if (this.bucket.compareAndSet(l, l - 1L)) {
                return true;
            }
            l = this.bucket.longValue();
        }
        return false;
    }

    private boolean tryAcquireSucceed() {
        long l = this.bucket.longValue();
        while (l <= 0L || !this.bucket.compareAndSet(l, l - 1L)) {
            this.sleep();
            l = this.bucket.longValue();
        }
        return true;
    }

    private void sleep() {
        if (this.rule.getUnit().toMillis(this.rule.getPeriod()) < 1L) {
            return;
        }
        try {
            Thread.sleep(this.rule.getUnit().toMillis(this.rule.getPeriod()));
        }
        catch (InterruptedException e) {
            LogUtils.error((Throwable)e);
        }
    }

    private void putPointBucket() {
        if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(true);
        }
        if (this.rule.getLimit() == 0L || !this.rule.getLimiterModel().equals((Object)LimiterModel.POINT)) {
            return;
        }
        this.scheduledFuture = this.config.getScheduledThreadExecutor().scheduleAtFixedRate(() -> this.bucket.set(this.rule.getLimit()), this.rule.getInitialDelay(), this.rule.getPeriod(), this.rule.getUnit());
    }

    private void putCloudBucket() {
        if (!this.rule.getLimiterModel().equals((Object)LimiterModel.CLOUD) || (double)this.bucket.get() / 1.0 * (double)this.rule.getBatch() > this.rule.getRemaining()) {
            return;
        }
        this.config.getScheduledThreadExecutor().execute(() -> {
            if ((double)this.bucket.get() / 1.0 * (double)this.rule.getBatch() <= this.rule.getRemaining()) {
                AtomicLong atomicLong = this.bucket;
                synchronized (atomicLong) {
                    String result;
                    if ((double)this.bucket.get() / 1.0 * (double)this.rule.getBatch() <= this.rule.getRemaining() && (result = this.config.getTicketServer().connect(RateLimiterConfig.http_token, JSON.toJSONString((Object)this.rule))) != null) {
                        this.bucket.getAndAdd(Long.parseLong(result));
                    }
                }
            }
        });
    }

    @Override
    public String getId() {
        return this.rule.getId();
    }

    @Override
    public RateLimiterRule getRule() {
        return this.rule;
    }
}

