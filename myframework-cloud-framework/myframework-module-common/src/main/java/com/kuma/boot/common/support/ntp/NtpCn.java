/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.ntp;

import com.kuma.boot.common.support.ntp.Ntp;

public class NtpCn {
    public static final String DEFAULT_TIME_SERVER = "time.7x24s.com";
    public static final String DEFAULT_TIME_SERVER_SH_IP = "203.107.6.88";
    private static final Ntp INSTANCE = new Ntp("203.107.6.88");

    public static long currentTimeMillis() {
        return INSTANCE.currentMillis();
    }

    public static long plusSeconds(long seconds) {
        return NtpCn.currentTimeMillis() + seconds * 1000L;
    }
}

