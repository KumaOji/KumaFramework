/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.io;

import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.utils.common.RegexUtils;
import com.kuma.boot.common.utils.exception.ExceptionUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;

public class NetUtils {
    public static final String LOCAL_LOOPBACK_HOST = "127.0.0.1";
    public static final String LOCALHOST = "127.0.0.1";
    private static final String LOCAL_HOST;

    public static String getHostName() {
        String hostname;
        try {
            InetAddress address = InetAddress.getLocalHost();
            hostname = address.getHostName();
            if (!StringUtils.hasText(hostname)) {
                hostname = address.toString();
            }
        }
        catch (UnknownHostException ignore) {
            hostname = "127.0.0.1";
        }
        return hostname;
    }

    public static String getHostIp() {
        String hostAddress;
        try {
            InetAddress address = NetUtils.getLocalHostLanAddress();
            hostAddress = address.getHostAddress();
            if (!StringUtils.hasText(hostAddress)) {
                hostAddress = address.toString();
            }
        }
        catch (UnknownHostException ignore) {
            hostAddress = "127.0.0.1";
        }
        return hostAddress;
    }

    private static InetAddress getLocalHostLanAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            Enumeration<NetworkInterface> iFaces = NetworkInterface.getNetworkInterfaces();
            while (iFaces.hasMoreElements()) {
                NetworkInterface iFace = iFaces.nextElement();
                Enumeration<InetAddress> inetAdders = iFace.getInetAddresses();
                while (inetAdders.hasMoreElements()) {
                    InetAddress inetAddr = inetAdders.nextElement();
                    if (inetAddr.isLoopbackAddress()) continue;
                    if (inetAddr.isSiteLocalAddress()) {
                        return inetAddr;
                    }
                    if (candidateAddress != null) continue;
                    candidateAddress = inetAddr;
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        }
        catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + String.valueOf(e));
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }

    public static boolean tryPort(int port) {
        boolean bl;
        ServerSocket ignore = new ServerSocket(port);
        try {
            bl = true;
        }
        catch (Throwable throwable) {
            try {
                try {
                    ignore.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (Exception e) {
                return false;
            }
        }
        ignore.close();
        return bl;
    }

    public static InetAddress getInetAddress(String ip) {
        try {
            return InetAddress.getByName(ip);
        }
        catch (UnknownHostException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static boolean isInternalIp(String ip) {
        return NetUtils.isInternalIp(NetUtils.getInetAddress(ip));
    }

    public static boolean isInternalIp(InetAddress address) {
        if (NetUtils.isLocalIp(address)) {
            return true;
        }
        return NetUtils.isInternalIp(address.getAddress());
    }

    public static boolean isLocalIp(InetAddress address) {
        return address.isAnyLocalAddress() || address.isLoopbackAddress() || address.isSiteLocalAddress();
    }

    public static boolean isInternalIp(byte[] addr) {
        byte b0 = addr[0];
        byte b1 = addr[1];
        int section1 = 10;
        int section2 = -84;
        int section3 = 16;
        int section4 = 31;
        int section5 = -64;
        int section6 = -88;
        switch (b0) {
            case 10: {
                return true;
            }
            case -84: {
                if (b1 >= 16 && b1 <= 31) {
                    return true;
                }
            }
            case -64: {
                if (b1 != -88) break;
                return true;
            }
        }
        return false;
    }

    public static String getLocalLoopbackHost() {
        return "127.0.0.1";
    }

    public static boolean isReachable(String remoteInetAddress) {
        return NetUtils.isReachable(remoteInetAddress, 5000);
    }

    public static boolean isReachable(String remoteInetAddress, int timeoutInMills) {
        boolean reachable = false;
        try {
            InetAddress address = InetAddress.getByName(remoteInetAddress);
            reachable = address.isReachable(timeoutInMills);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return reachable;
    }

    public static void assertOnLine() {
        String address = "translate.google.cn";
        try {
            InetAddress inetAddress = InetAddress.getByName("translate.google.cn");
        }
        catch (UnknownHostException e) {
            throw new RuntimeException("The net work is broken, check your network or set isCommentWhenNetworkBroken=true.");
        }
    }

    public static String getLocalIp() {
        InetAddress inetAddress = NetUtils.findLocalAddress();
        if (inetAddress == null) {
            return null;
        }
        String ip = inetAddress.getHostAddress();
        if (RegexUtils.isIp(ip)) {
            return ip;
        }
        return null;
    }

    private static InetAddress findLocalAddress() {
        String preferNamePrefix = "bond0";
        String defaultNicList = "bond0,eth0,em0,en0,em1,br0,eth1,em2,en1,eth2,em3,en2,eth3,em4,en3";
        InetAddress resultAddress = null;
        HashMap<String, NetworkInterface> candidateInterfaces = new HashMap<String, NetworkInterface>();
        try {
            String[] allInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allInterfaces.hasMoreElements()) {
                NetworkInterface nic = allInterfaces.nextElement();
                if (!nic.isUp() || !nic.supportsMulticast()) continue;
                String name = nic.getName();
                if (name.startsWith(preferNamePrefix)) {
                    resultAddress = NetUtils.findAvailableAddress(nic);
                    if (resultAddress == null) continue;
                    return resultAddress;
                }
                candidateInterfaces.put(name, nic);
            }
            for (String nifName : defaultNicList.split(",")) {
                NetworkInterface nic = (NetworkInterface)candidateInterfaces.get(nifName);
                if (nic == null || (resultAddress = NetUtils.findAvailableAddress(nic)) == null) continue;
                return resultAddress;
            }
            return null;
        }
        catch (SocketException e) {
            throw new BootException(e);
        }
    }

    private static InetAddress findAvailableAddress(NetworkInterface nic) {
        Enumeration<InetAddress> inetAddresses = nic.getInetAddresses();
        while (inetAddresses.hasMoreElements()) {
            InetAddress inetAddress = inetAddresses.nextElement();
            if (inetAddress instanceof Inet6Address || inetAddress.isLoopbackAddress()) continue;
            return inetAddress;
        }
        return null;
    }

    static {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        }
        catch (UnknownHostException e) {
            throw new BootException(e);
        }
        LOCAL_HOST = address.getHostAddress();
    }
}

