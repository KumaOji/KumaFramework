/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.io;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class MacAddressUtils {
    public static List<String> getMacList() throws Exception {
        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
        StringBuilder sb = new StringBuilder();
        ArrayList<String> tmpMacList = new ArrayList<String>();
        while (en.hasMoreElements()) {
            NetworkInterface iface = en.nextElement();
            List<InterfaceAddress> addrs = iface.getInterfaceAddresses();
            for (InterfaceAddress addr : addrs) {
                byte[] mac;
                InetAddress ip = addr.getAddress();
                NetworkInterface network = NetworkInterface.getByInetAddress(ip);
                if (network == null || (mac = network.getHardwareAddress()) == null) continue;
                sb.delete(0, sb.length());
                for (int i = 0; i < mac.length; ++i) {
                    sb.append(String.format("%02X%s", mac[i], i < mac.length - 1 ? "-" : ""));
                }
                tmpMacList.add(sb.toString());
            }
        }
        if (tmpMacList.size() <= 0) {
            return tmpMacList;
        }
        return tmpMacList.stream().distinct().toList();
    }
}

