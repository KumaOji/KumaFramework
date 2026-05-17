package com.kuma.boot.core.utils.io;

import java.net.*;
import java.util.*;

import org.springframework.stereotype.Component;

/**
 * NetworkInterfaceUtil
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class NetworkInterfaceUtil {

    /**
     * 获取WiFi IP地址（跳过虚拟网卡）
     */
    public static String getWifiIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (isWifiInterface(iface)) {
                    Enumeration<InetAddress> addresses = iface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                            return addr.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException("Failed to get network interfaces", e);
        }
        return "127.0.0.1";
    }

    /**
     * 判断是否为WiFi接口（兼容多平台）
     */
    private static boolean isWifiInterface( NetworkInterface iface ) throws SocketException {
        String name = iface.getDisplayName().toLowerCase();
        return iface.isUp() &&
                !iface.isLoopback() &&
                !name.contains("virtual") &&
                !name.contains("vether") &&
                !name.contains("docker") &&
                !name.contains("hyper-v") &&
                ( name.contains("wifi") ||
                        name.contains("wireless") ||
                        name.contains("wlan") ||
                        name.contains("wi-fi") );
    }

    /**
     * 打印所有网络接口（调试用）
     */
    public static void logNetworkInterfaces() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                System.out.printf(
                        "Interface: %-20s (Up: %-5b, Virtual: %-5b, DisplayName: %s)%n",
                        iface.getName(),
                        iface.isUp(),
                        isVirtualInterface(iface),
                        iface.getDisplayName()
                );
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private static boolean isVirtualInterface( NetworkInterface iface ) {
        String name = iface.getDisplayName().toLowerCase();
        return name.contains("virtual") ||
                name.contains("vether") ||
                name.contains("docker") ||
                name.contains("hyper-v");
    }
}
