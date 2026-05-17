/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.utils.ip;

import tools.jackson.databind.JsonNode;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Objects;

/**
 * IP工具类
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 16:41:13
 */
public class IpUtils {

    private IpUtils() {}

    /** IP_LOCAL */
    private static final boolean IP_LOCAL = false;

    /**
     * 根据ip获取详细地址
     * @param ip ip
     * @return ip地址
     * @since 2022-03-23 08:19:10
     */
    public static String getCityInfo(String ip) {
        if (IP_LOCAL) {
            // 待开发
            return null;
        } else {
            return getHttpCityInfo(ip);
        }
    }

    /**
     * 根据ip获取详细地址
     * @param ip ip
     * @return 详细地址
     * @since 2022-03-23 08:17:23
     */
    public static String getHttpCityInfo(String ip) {
        String api = String.format("http://whois.pconline.com.cn/ipJson.jsp?ip=%s&json=true", ip);
        JsonNode node = JacksonUtils.parse(httpGet(api, "gbk"));
        if (Objects.nonNull(node)) {
            LogUtils.info(node.toString());
            return node.get("addr").toString();
        }

        return null;
    }

    private static String httpGet(String url, String charset) {
        try {
            HttpURLConnection conn = (HttpURLConnection) URI.create(url).toURL().openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            try (InputStreamReader reader =
                    new InputStreamReader(conn.getInputStream(), Charset.forName(charset))) {
                char[] buf = new char[4096];
                StringBuilder sb = new StringBuilder();
                int n;
                while ((n = reader.read(buf)) != -1) sb.append(buf, 0, n);
                return sb.toString();
            }
        } catch (Exception e) {
            LogUtils.error("IpUtils httpGet error: {}", e.getMessage());
            return null;
        }
    }

    public static final String DEFAULT_IP = "127.0.0.1";

    private static String ip = null;

    public static String getLocalIpByNetCard() {
        try {
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();

            while (e.hasMoreElements()) {
                NetworkInterface item = e.nextElement();

                for (InterfaceAddress address : item.getInterfaceAddresses()) {
                    if (!item.isLoopback()
                            && item.isUp()
                            && address.getAddress() instanceof Inet4Address) {
                        return ((Inet4Address) address.getAddress()).getHostAddress();
                    }
                }
            }

            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException | SocketException var4) {
            throw new RuntimeException(var4);
        }
    }

    public static String getLocalIp() {
        if (ip == null) {
            try {
                ip = getLocalIpByNetCard();
            } catch (Exception var1) {
                LogUtils.error("get local server ip error!");
                ip = DEFAULT_IP;
            }
        }

        return ip;
    }
}
