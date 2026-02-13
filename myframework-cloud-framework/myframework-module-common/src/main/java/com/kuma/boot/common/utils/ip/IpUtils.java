/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  tools.jackson.databind.JsonNode
 */
package com.kuma.boot.common.utils.ip;

import com.kuma.boot.common.utils.io.HttpUtils;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Objects;
import tools.jackson.databind.JsonNode;

public class IpUtils {
    private static final boolean IP_LOCAL = false;
    public static final String DEFAULT_IP = "127.0.0.1";
    private static String ip = null;

    private IpUtils() {
    }

    public static String getCityInfo(String ip) {
        return IpUtils.getHttpCityInfo(ip);
    }

    public static String getHttpCityInfo(String ip) {
        String api = String.format("http://whois.pconline.com.cn/ipJson.jsp?ip=%s&json=true", ip);
        JsonNode node = JacksonUtils.parse((String)HttpUtils.getRequest(api, "gbk"));
        if (Objects.nonNull(node)) {
            LogUtils.info(node.toString(), new Object[0]);
            return node.get("addr").toString();
        }
        return null;
    }

    public static String getLocalIpByNetCard() {
        try {
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface item = e.nextElement();
                for (InterfaceAddress address : item.getInterfaceAddresses()) {
                    if (item.isLoopback() || !item.isUp() || !(address.getAddress() instanceof Inet4Address)) continue;
                    return ((Inet4Address)address.getAddress()).getHostAddress();
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        }
        catch (SocketException | UnknownHostException var4) {
            throw new RuntimeException(var4);
        }
    }

    public static String getLocalIp() {
        if (ip == null) {
            try {
                ip = IpUtils.getLocalIpByNetCard();
            }
            catch (Exception var1) {
                LogUtils.error("get local server ip error!", new Object[0]);
                ip = DEFAULT_IP;
            }
        }
        return ip;
    }
}

