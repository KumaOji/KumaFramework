/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package com.kuma.boot.ip2region.utils;

import com.kuma.boot.ip2region.model.IpInfo;
import java.util.Arrays;
import java.util.function.Function;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

public class IpInfoUtil {
    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\|");
    private static final Pattern DOT_PATTERN = Pattern.compile("\\.");
    private static final Pattern T_PATTERN = Pattern.compile("\\t");

    public static String[] getIpV4Part(String ip) {
        return DOT_PATTERN.split(ip);
    }

    public static @Nullable IpInfo toIpInfo(@Nullable String region) {
        if (region == null) {
            return null;
        }
        IpInfo ipInfo = new IpInfo();
        String[] splitInfoArr = SPLIT_PATTERN.split(region);
        if (splitInfoArr.length < 5) {
            splitInfoArr = Arrays.copyOf(splitInfoArr, 5);
        }
        ipInfo.setCountry(IpInfoUtil.filterZero(splitInfoArr[0]));
        ipInfo.setRegion(IpInfoUtil.filterZero(splitInfoArr[1]));
        ipInfo.setProvince(IpInfoUtil.filterZero(splitInfoArr[2]));
        ipInfo.setCity(IpInfoUtil.filterZero(splitInfoArr[3]));
        ipInfo.setIsp(IpInfoUtil.filterZero(splitInfoArr[4]));
        return ipInfo;
    }

    public static @Nullable IpInfo toIpV6Info(String[] ipRecord) {
        IpInfo ipInfo = new IpInfo();
        String info1 = ipRecord[0];
        String[] splitInfoArr = T_PATTERN.split(info1);
        if (splitInfoArr.length < 4) {
            splitInfoArr = Arrays.copyOf(splitInfoArr, 4);
        }
        ipInfo.setCountry(splitInfoArr[0]);
        ipInfo.setProvince(splitInfoArr[1]);
        ipInfo.setCity(splitInfoArr[2]);
        ipInfo.setRegion(splitInfoArr[3]);
        ipInfo.setIsp(ipRecord[1]);
        return ipInfo;
    }

    private static @Nullable String filterZero(@Nullable String info) {
        if (info == null || "0".equals(info)) {
            return null;
        }
        return info;
    }

    public static @Nullable String readInfo(@Nullable IpInfo ipInfo, Function<IpInfo, String> function) {
        if (ipInfo == null) {
            return null;
        }
        return function.apply(ipInfo);
    }
}

