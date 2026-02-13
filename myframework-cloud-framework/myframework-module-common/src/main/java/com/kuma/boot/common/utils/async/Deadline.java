/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.async;

import com.kuma.boot.common.utils.async.Clock;
import com.kuma.boot.common.utils.async.SystemClock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

public class Deadline {
    private final long timeNanos;
    private final Clock clock;

    private Deadline(long deadline, Clock clock) {
        this.timeNanos = deadline;
        this.clock = clock;
    }

    public Deadline plus(Duration other) {
        return new Deadline(Deadline.addHandlingOverflow(this.timeNanos, other.toNanos()), this.clock);
    }

    public Duration timeLeft() {
        return Duration.ofNanos(Math.subtractExact(this.timeNanos, this.clock.relativeTimeNanos()));
    }

    public Duration timeLeftIfAny() throws TimeoutException {
        long nanos = Math.subtractExact(this.timeNanos, this.clock.relativeTimeNanos());
        if (nanos <= 0L) {
            throw new TimeoutException();
        }
        return Duration.ofNanos(nanos);
    }

    public boolean hasTimeLeft() {
        return !this.isOverdue();
    }

    public boolean isOverdue() {
        return this.timeNanos <= this.clock.relativeTimeNanos();
    }

    public static Deadline now() {
        return new Deadline(System.nanoTime(), SystemClock.getInstance());
    }

    public static Deadline fromNow(Duration duration) {
        return new Deadline(Deadline.addHandlingOverflow(System.nanoTime(), duration.toNanos()), SystemClock.getInstance());
    }

    public static Deadline fromNowWithClock(Duration duration, Clock clock) {
        return new Deadline(Deadline.addHandlingOverflow(clock.relativeTimeNanos(), duration.toNanos()), clock);
    }

    public String toString() {
        return LocalDateTime.now().plus(this.timeLeft()).toString();
    }

    private static long addHandlingOverflow(long x, long y) {
        long r = x + y;
        if (((x ^ r) & (y ^ r)) < 0L) {
            return Long.MAX_VALUE;
        }
        return x + y;
    }
}

