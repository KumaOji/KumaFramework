/*
 *  com.alibaba.fastjson2.JSON
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.kuma.boot.ratelimit.ratelimitsnowjean.core.observer;

import com.alibaba.fastjson2.JSON;
import com.kuma.boot.ratelimit.ratelimitsnowjean.commoon.entity.RateLimiterRule;
import com.kuma.boot.ratelimit.ratelimitsnowjean.commoon.enums.LimiterModel;
import com.kuma.boot.ratelimit.ratelimitsnowjean.core.config.RateLimiterConfig;
import com.kuma.boot.ratelimit.ratelimitsnowjean.core.exception.SnowJeanException;
import com.kuma.boot.ratelimit.ratelimitsnowjean.core.limiter.RateLimiter;
import com.kuma.boot.ratelimit.ratelimitsnowjean.monitor.entity.MonitorBean;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RateLimiterObserver {
    private static Map<String, RateLimiter> map = new ConcurrentHashMap<String, RateLimiter>();
    private static Logger logger = LoggerFactory.getLogger(RateLimiterObserver.class);

    public static void registered(RateLimiter limiter, RateLimiterConfig config) {
        if (map.containsKey(limiter.getId())) {
            throw new SnowJeanException("Repeat registration for current limiting rules:" + limiter.getId());
        }
        map.put(limiter.getId(), limiter);
        if (!limiter.getRule().getLimiterModel().equals((Object)LimiterModel.CLOUD)) {
            return;
        }
        RateLimiterObserver.update(limiter, config);
        RateLimiterObserver.monitor(limiter, config);
    }

    public static Map<String, RateLimiter> getMap() {
        return map;
    }

    private static void update(RateLimiter limiter, RateLimiterConfig config) {
        config.getScheduledThreadExecutor().scheduleWithFixedDelay(() -> {
            String rules = config.getTicketServer().connect(RateLimiterConfig.http_heart, JSON.toJSONString((Object)limiter.getRule()));
            if (rules == null) {
                logger.debug("update limiter fail, automatically switch to local current limit");
                RateLimiterRule rule = limiter.getRule();
                rule.setLimiterModel(LimiterModel.POINT);
                limiter.init(rule);
                return;
            }
            RateLimiterRule rateLimiterRule = (RateLimiterRule)JSON.parseObject((String)rules, RateLimiterRule.class);
            if (rateLimiterRule.getVersion() > limiter.getRule().getVersion()) {
                logger.info("update rule version: {} -> {}", (Object)limiter.getRule().getVersion(), (Object)rateLimiterRule.getVersion());
                map.get(limiter.getId()).init(rateLimiterRule);
            } else if (rateLimiterRule.getLimiterModel().equals((Object)LimiterModel.POINT)) {
                rateLimiterRule.setLimiterModel(LimiterModel.CLOUD);
                map.get(limiter.getId()).init(rateLimiterRule);
            }
        }, 0L, 1L, TimeUnit.SECONDS);
    }

    private static void monitor(RateLimiter limiter, RateLimiterConfig config) {
        config.getScheduledThreadExecutor().scheduleWithFixedDelay(() -> {
            if (limiter.getRule().getMonitor() == 0L) {
                return;
            }
            List<MonitorBean> monitorBeans = limiter.getMonitorService().getAndDelete();
            if (monitorBeans.size() < 1) {
                return;
            }
            String result = config.getTicketServer().connect(RateLimiterConfig.http_monitor, JSON.toJSONString(monitorBeans));
            if (result == null) {
                logger.debug("http_monitor data update fail");
            }
        }, 0L, 3L, TimeUnit.SECONDS);
    }
}

