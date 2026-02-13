/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.io;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetworkInterfaceUtil {
    public static String getWifiIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (!NetworkInterfaceUtil.isWifiInterface(iface)) continue;
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (!(addr instanceof Inet4Address) || addr.isLoopbackAddress()) continue;
                    return addr.getHostAddress();
                }
            }
        }
        catch (SocketException e) {
            throw new RuntimeException("Failed to get network interfaces", e);
        }
        return "127.0.0.1";
    }

    private static boolean isWifiInterface(NetworkInterface iface) throws SocketException {
        String name = iface.getDisplayName().toLowerCase();
        return iface.isUp() && !iface.isLoopback() && !name.contains("virtual") && !name.contains("vether") && !name.contains("docker") && !name.contains("hyper-v") && (name.contains("wifi") || name.contains("wireless") || name.contains("wlan") || name.contains("wi-fi"));
    }

    public static void logNetworkInterfaces() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                System.out.printf("Interface: %-20s (Up: %-5b, Virtual: %-5b, DisplayName: %s)%n", iface.getName(), iface.isUp(), NetworkInterfaceUtil.isVirtualInterface(iface), iface.getDisplayName());
            }
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private static boolean isVirtualInterface(NetworkInterface iface) {
        String name = iface.getDisplayName().toLowerCase();
        return name.contains("virtual") || name.contains("vether") || name.contains("docker") || name.contains("hyper-v");
    }
}

