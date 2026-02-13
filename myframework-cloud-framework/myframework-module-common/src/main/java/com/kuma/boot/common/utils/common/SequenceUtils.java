/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.date.SystemClock
 *  cn.hutool.core.lang.Assert
 *  cn.hutool.core.util.StrUtil
 */
package com.kuma.boot.common.utils.common;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.concurrent.ThreadLocalRandom;

public final class SequenceUtils {
    private final long epoch = 1288834974657L;
    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;
    private final long maxWorkerId = 31L;
    private final long maxDatacenterId = 31L;
    private final long sequenceBits = 12L;
    private final long workerIdShift = 12L;
    private final long datacenterIdShift = 17L;
    private final long timestampLeftShift = 22L;
    private final long sequenceMask = 4095L;
    private long workerId = 0L;
    private long datacenterId = 0L;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    private long timestampOffset = 5L;

    public SequenceUtils() {
        this.datacenterId = SequenceUtils.getDatacenterId(31L);
        this.workerId = SequenceUtils.getMaxWorkerId(this.datacenterId, 31L);
    }

    public SequenceUtils(long workerId, long datacenterId) {
        Assert.isFalse((workerId > 31L || workerId < 0L ? 1 : 0) != 0, (String)String.format("worker Id can't be greater than %d or less than 0", 31L), (Object[])new Object[0]);
        Assert.isFalse((datacenterId > 31L || datacenterId < 0L ? 1 : 0) != 0, (String)String.format("datacenter Id can't be greater than %d or less than 0", 31L), (Object[])new Object[0]);
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    protected static long getMaxWorkerId(long datacenterId, long maxWorkerId) {
        StringBuilder mpid = new StringBuilder();
        mpid.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (StrUtil.isNotEmpty((CharSequence)name)) {
            mpid.append(name.split("@")[0]);
        }
        return (long)(mpid.toString().hashCode() & 0xFFFF) % (maxWorkerId + 1L);
    }

    protected static long getDatacenterId(long maxDatacenterId) {
        long id = 0L;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                if (null != mac) {
                    id = (0xFFL & (long)mac[mac.length - 1] | 0xFF00L & (long)mac[mac.length - 2] << 8) >> 6;
                    id %= maxDatacenterId + 1L;
                }
            }
        }
        catch (Exception e) {
            LogUtils.warn(" getDatacenterId: " + e.getMessage(), new Object[0]);
        }
        return id;
    }

    public synchronized long nextId() {
        long timestamp;
        block8: {
            timestamp = this.timeGen();
            if (timestamp < this.lastTimestamp) {
                long offset = this.lastTimestamp - timestamp;
                if (offset <= this.timestampOffset) {
                    try {
                        this.wait(offset << 1);
                        timestamp = this.timeGen();
                        if (timestamp < this.lastTimestamp) {
                            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
                        }
                        break block8;
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
            }
        }
        if (this.lastTimestamp == timestamp) {
            this.sequence = this.sequence + 1L & 0xFFFL;
            if (this.sequence == 0L) {
                timestamp = this.tilNextMillis(this.lastTimestamp);
            }
        } else {
            this.sequence = ThreadLocalRandom.current().nextLong(1L, 3L);
        }
        this.lastTimestamp = timestamp;
        return timestamp - 1288834974657L << 22 | this.datacenterId << 17 | this.workerId << 12 | this.sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return SystemClock.now();
    }
}

