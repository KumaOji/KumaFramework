/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitsnowjean.commoon.entity;

import com.kuma.boot.ratelimit.ratelimitsnowjean.commoon.enums.AcquireModel;
import com.kuma.boot.ratelimit.ratelimitsnowjean.commoon.enums.LimiterModel;
import com.kuma.boot.ratelimit.ratelimitsnowjean.commoon.enums.RuleAuthority;
import java.util.concurrent.TimeUnit;

public class RateLimiterRuleBuilder {
    private RateLimiterRule rateLimiterRule = new RateLimiterRule();

    public RateLimiterRuleBuilder setApp(String app) {
        this.rateLimiterRule.setApp(app);
        return this;
    }

    public RateLimiterRuleBuilder setId(String id) {
        this.rateLimiterRule.setId(id);
        return this;
    }

    public RateLimiterRuleBuilder setLimit(long limit) {
        this.rateLimiterRule.setLimit(limit);
        return this;
    }

    public RateLimiterRuleBuilder setPeriod(long period) {
        this.rateLimiterRule.setPeriod(period);
        return this;
    }

    public RateLimiterRuleBuilder setUnit(TimeUnit unit) {
        this.rateLimiterRule.setUnit(unit);
        return this;
    }

    public RateLimiterRuleBuilder setInitialDelay(long initialDelay) {
        this.rateLimiterRule.setInitialDelay(initialDelay);
        return this;
    }

    public RateLimiterRuleBuilder setBatch(long batch) {
        this.rateLimiterRule.setBatch(batch);
        return this;
    }

    public RateLimiterRuleBuilder setRemaining(double remaining) {
        this.rateLimiterRule.setRemaining(remaining);
        return this;
    }

    public RateLimiterRuleBuilder setMonitor(long monitor) {
        this.rateLimiterRule.setMonitor(monitor);
        return this;
    }

    public RateLimiterRuleBuilder setLimitUser(String[] limitUser) {
        this.rateLimiterRule.setLimitUser(limitUser);
        return this;
    }

    public RateLimiterRuleBuilder setRuleAuthority(RuleAuthority ruleAuthority) {
        this.rateLimiterRule.setRuleAuthority(ruleAuthority);
        return this;
    }

    public RateLimiterRuleBuilder setAcquireModel(AcquireModel acquireModel) {
        this.rateLimiterRule.setAcquireModel(acquireModel);
        return this;
    }

    public RateLimiterRuleBuilder setLimiterModel(LimiterModel limiterModel) {
        this.rateLimiterRule.setLimiterModel(limiterModel);
        return this;
    }

    public RateLimiterRule build() {
        RateLimiterRuleBuilder.check(this.rateLimiterRule);
        return this.rateLimiterRule;
    }

    public static void check(RateLimiterRule rateLimiterRule) {
        assert (rateLimiterRule.getBatch() > 0L);
        assert (rateLimiterRule.getRemaining() >= 0.0 && rateLimiterRule.getRemaining() <= 1.0);
        assert (rateLimiterRule.getPeriod() >= 0L);
        assert (rateLimiterRule.getInitialDelay() >= 0L);
        assert (rateLimiterRule.getMonitor() >= 0L);
    }
}

