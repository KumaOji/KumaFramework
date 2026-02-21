/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package com.kuma.boot.ip2region.model;

import com.kuma.boot.ip2region.utils.IpInfoUtil;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;

public interface Ip2regionSearcher {
    public @Nullable IpInfo memorySearch(long var1);

    public @Nullable IpInfo memorySearch(String var1);

    default public @Nullable String getInfo(long ip, Function<IpInfo, String> function) {
        return IpInfoUtil.readInfo(this.memorySearch(ip), function);
    }

    default public @Nullable String getInfo(String ip, Function<IpInfo, String> function) {
        return IpInfoUtil.readInfo(this.memorySearch(ip), function);
    }

    default public @Nullable String getAddress(long ip) {
        return this.getInfo(ip, IpInfo::getAddress);
    }

    default public @Nullable String getAddress(String ip) {
        return this.getInfo(ip, IpInfo::getAddress);
    }

    default public @Nullable String getAddressAndIsp(long ip) {
        return this.getInfo(ip, IpInfo::getAddressAndIsp);
    }

    default public @Nullable String getAddressAndIsp(String ip) {
        return this.getInfo(ip, IpInfo::getAddressAndIsp);
    }
}

