/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.network;

import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

public final class IpAddressUtils {
    public static List<Inet4Address> getLocalIp4AddressFromNetworkInterface() {
        ArrayList<Inet4Address> addresses = new ArrayList<Inet4Address>(1);
        try {
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            if (e != null) {
                while (e.hasMoreElements()) {
                    NetworkInterface n = e.nextElement();
                    if (!IpAddressUtils.isValidInterface(n)) continue;
                    Enumeration<InetAddress> ee = n.getInetAddresses();
                    while (ee.hasMoreElements()) {
                        InetAddress i = ee.nextElement();
                        if (!IpAddressUtils.isValidAddress(i)) continue;
                        addresses.add((Inet4Address)i);
                    }
                }
            }
        }
        catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
        return addresses;
    }

    private static boolean isValidInterface(NetworkInterface ni) {
        try {
            return !ni.isLoopback() && !ni.isPointToPoint() && ni.isUp() && !ni.isVirtual();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isValidAddress(InetAddress address) {
        return address instanceof Inet4Address && address.isSiteLocalAddress() && !address.isLoopbackAddress();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Optional<Inet4Address> getIpBySocket() {
        try (DatagramSocket socket = new DatagramSocket();){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            if (socket.getLocalAddress() instanceof Inet4Address) {
                Optional<Inet4Address> optional = Optional.of((Inet4Address)socket.getLocalAddress());
                return optional;
            }
            Optional<Inet4Address> optional = Optional.empty();
            return optional;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<Inet4Address> getLocalIp4Address() {
        List<Inet4Address> ipByNi = IpAddressUtils.getLocalIp4AddressFromNetworkInterface();
        if (ipByNi.size() != 1) {
            Optional<Inet4Address> ipBySocketOpt = IpAddressUtils.getIpBySocket();
            if (ipBySocketOpt.isPresent()) {
                return ipBySocketOpt;
            }
            return ipByNi.isEmpty() ? Optional.empty() : Optional.of(ipByNi.get(0));
        }
        return Optional.of(ipByNi.get(0));
    }

    public static String getLocalIp() {
        return IpAddressUtils.getLocalIp4Address().map(Inet4Address::getHostAddress).orElseThrow(() -> new UnsupportedOperationException("\u65e0\u6cd5\u83b7\u53d6\u672c\u5730IP"));
    }

    public static String getLocalIp(String defaultIp) {
        return IpAddressUtils.getLocalIp4Address().map(Inet4Address::getHostAddress).orElse(defaultIp);
    }
}

