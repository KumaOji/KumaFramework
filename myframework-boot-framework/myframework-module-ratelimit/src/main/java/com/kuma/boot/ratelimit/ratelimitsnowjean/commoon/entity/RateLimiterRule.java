/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitsnowjean.commoon.entity;

import com.kuma.boot.ratelimit.ratelimitsnowjean.commoon.enums.AcquireModel;
import com.kuma.boot.ratelimit.ratelimitsnowjean.commoon.enums.LimiterModel;
import com.kuma.boot.ratelimit.ratelimitsnowjean.commoon.enums.RuleAuthority;
import java.util.concurrent.TimeUnit;

public class RateLimiterRule
implements Comparable<RateLimiterRule> {
    private String app = "Application";
    private String id = "id";
    private String name;
    private boolean enable;
    private long limit;
    private long period = 1L;
    private long initialDelay = 0L;
    private TimeUnit unit = TimeUnit.SECONDS;
    private long batch = 1L;
    private double remaining = 0.5;
    private long monitor = 10L;
    private AcquireModel acquireModel = AcquireModel.FAILFAST;
    private LimiterModel limiterModel = LimiterModel.POINT;
    private RuleAuthority ruleAuthority = RuleAuthority.NULL;
    private String[] limitUser;
    private int number;
    private long version;

    public String getApp() {
        return this.app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLimit() {
        return this.limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public long getPeriod() {
        return this.period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public long getInitialDelay() {
        return this.initialDelay;
    }

    public void setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
    }

    public TimeUnit getUnit() {
        return this.unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    public long getBatch() {
        return this.batch;
    }

    public void setBatch(long batch) {
        this.batch = batch;
    }

    public double getRemaining() {
        return this.remaining;
    }

    public void setRemaining(double remaining) {
        this.remaining = remaining;
    }

    public long getMonitor() {
        return this.monitor;
    }

    public void setMonitor(long monitor) {
        this.monitor = monitor;
    }

    public AcquireModel getAcquireModel() {
        return this.acquireModel;
    }

    public void setAcquireModel(AcquireModel acquireModel) {
        this.acquireModel = acquireModel;
    }

    public LimiterModel getLimiterModel() {
        return this.limiterModel;
    }

    public void setLimiterModel(LimiterModel limiterModel) {
        this.limiterModel = limiterModel;
    }

    public RuleAuthority getRuleAuthority() {
        return this.ruleAuthority;
    }

    public void setRuleAuthority(RuleAuthority ruleAuthority) {
        this.ruleAuthority = ruleAuthority;
    }

    public String[] getLimitUser() {
        return this.limitUser;
    }

    public void setLimitUser(String[] limitUser) {
        this.limitUser = limitUser;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getVersion() {
        return this.version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public boolean isEnable() {
        return this.enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public int compareTo(RateLimiterRule o) {
        if (this.version < o.getVersion()) {
            return -1;
        }
        if (this.version == o.getVersion()) {
            return 0;
        }
        return 1;
    }
}

