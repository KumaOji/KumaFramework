//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.kuma.boot.cache.jetcache.autoconfigure.properties;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.ObjectUtils;

public class Expire {
    private Long duration = 1L;
    private TimeUnit unit;
    private Duration ttl;

    public Expire() {
        this.unit = TimeUnit.HOURS;
    }

    public Long getDuration() {
        return this.duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public TimeUnit getUnit() {
        return this.unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    public Duration getTtl() {
        if (ObjectUtils.isEmpty(this.ttl)) {
            this.ttl = this.convertToDuration(this.duration, this.unit);
        }

        return this.ttl;
    }

    private Duration convertToDuration(Long duration, TimeUnit timeUnit) {
        switch (timeUnit) {
            case DAYS -> {
                return Duration.ofDays(duration);
            }
            case HOURS -> {
                return Duration.ofHours(duration);
            }
            case SECONDS -> {
                return Duration.ofSeconds(duration);
            }
            default -> {
                return Duration.ofMinutes(duration);
            }
        }
    }
}
