/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.net.ntp.NTPUDPClient
 *  org.apache.commons.net.ntp.TimeInfo
 */
package com.kuma.boot.common.support.ntp;

import com.kuma.boot.common.support.ntp.NtpException;
import com.kuma.boot.common.utils.log.LogUtils;
import java.net.InetAddress;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

public class Ntp {
    private final long diff;

    public static long diff(String host) {
        try {
            NTPUDPClient client = new NTPUDPClient();
            TimeInfo time = client.getTime(InetAddress.getByName(host));
            long systemMillis = System.currentTimeMillis();
            long ntpMillis = time.getMessage().getTransmitTimeStamp().getTime();
            return ntpMillis - systemMillis;
        }
        catch (Exception e) {
            throw new NtpException("ntp\u521d\u59cb\u5316\u5f02\u5e38!", e);
        }
    }

    public Ntp(String host) {
        try {
            this.diff = Ntp.diff(host);
            LogUtils.warn("\u6388\u65f6\u4e2d\u5fc3\u65f6\u95f4\u4e0e\u7cfb\u7edf\u65f6\u95f4\u5dee\u4e3a {} \u6beb\u79d2", this.diff);
        }
        catch (Exception e) {
            throw new NtpException("ntp\u521d\u59cb\u5316\u5f02\u5e38!", e);
        }
    }

    public long currentMillis() {
        return System.currentTimeMillis() + this.diff;
    }

    public Ntp use(String host) {
        return new Ntp(host);
    }
}

