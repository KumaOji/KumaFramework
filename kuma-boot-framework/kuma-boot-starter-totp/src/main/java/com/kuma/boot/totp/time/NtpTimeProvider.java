/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.apache.commons.net.ntp.NTPUDPClient
 *  org.apache.commons.net.ntp.TimeInfo
 */
package com.kuma.boot.totp.time;

import com.kuma.boot.totp.exceptions.TimeProviderException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

public class NtpTimeProvider
implements TimeProvider {
    private final NTPUDPClient client;
    private final InetAddress ntpHost;

    public NtpTimeProvider(String ntpHostname) throws UnknownHostException {
        this(ntpHostname, 3000);
    }

    public NtpTimeProvider(String ntpHostname, int timeout) throws UnknownHostException {
        this(ntpHostname, timeout, "org.apache.commons.net.ntp.NTPUDPClient");
    }

    NtpTimeProvider(String ntpHostname, String dependentClass) throws UnknownHostException {
        this(ntpHostname, 3000, dependentClass);
    }

    private NtpTimeProvider(String ntpHostname, int timeout, String dependentClass) throws UnknownHostException {
        this.checkHasDependency(dependentClass);
        this.client = new NTPUDPClient();
        this.client.setDefaultTimeout(timeout);
        this.ntpHost = InetAddress.getByName(ntpHostname);
    }

    @Override
    public long getTime() throws TimeProviderException {
        TimeInfo timeInfo;
        try {
            timeInfo = this.client.getTime(this.ntpHost);
            timeInfo.computeDetails();
        }
        catch (Exception e) {
            throw new TimeProviderException("Failed to provide time from NTP server. See nested exception.", e);
        }
        if (timeInfo.getOffset() == null) {
            throw new TimeProviderException("Failed to calculate NTP offset");
        }
        return (System.currentTimeMillis() + timeInfo.getOffset()) / 1000L;
    }

    private void checkHasDependency(String dependentClass) {
        try {
            Class<?> clazz = Class.forName(dependentClass);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("The Apache Commons Net library must be on the classpath to use the NtpTimeProvider.");
        }
    }
}

