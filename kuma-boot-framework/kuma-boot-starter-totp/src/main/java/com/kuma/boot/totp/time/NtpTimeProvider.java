package com.kuma.boot.totp.time;

import com.kuma.boot.totp.exceptions.TimeProviderException;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;

public class NtpTimeProvider implements TimeProvider {

    private final NTPUDPClient client;
    private final InetAddress ntpHost;

    public NtpTimeProvider(String ntpHostname) throws UnknownHostException {
        this(ntpHostname, 3000);
    }

    public NtpTimeProvider(String ntpHostname, int timeoutMillis) throws UnknownHostException {
        this(ntpHostname, timeoutMillis, NTPUDPClient.class.getName());
    }

    NtpTimeProvider(String ntpHostname, String dependentClass) throws UnknownHostException {
        this(ntpHostname, 3000, dependentClass);
    }

    private NtpTimeProvider(String ntpHostname, int timeoutMillis, String dependentClass) throws UnknownHostException {
        checkHasDependency(dependentClass);
        this.client = new NTPUDPClient();
        this.client.setDefaultTimeout(Duration.ofMillis(timeoutMillis));
        this.ntpHost = InetAddress.getByName(ntpHostname);
    }

    @Override
    public long getTime() throws TimeProviderException {
        TimeInfo timeInfo;
        try {
            timeInfo = this.client.getTime(this.ntpHost);
            timeInfo.computeDetails();
        } catch (Exception e) {
            throw new TimeProviderException("Failed to provide time from NTP server. See nested exception.", e);
        }
        if (timeInfo.getOffset() == null) {
            throw new TimeProviderException("Failed to calculate NTP offset");
        }
        return (System.currentTimeMillis() + timeInfo.getOffset()) / 1000L;
    }

    private static void checkHasDependency(String dependentClass) {
        try {
            Class.forName(dependentClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("The Apache Commons Net library must be on the classpath to use the NtpTimeProvider.");
        }
    }
}
