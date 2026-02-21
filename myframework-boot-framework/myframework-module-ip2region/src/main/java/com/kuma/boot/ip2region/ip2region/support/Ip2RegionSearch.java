/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.json.JacksonUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.lionsoul.ip2region.xdb.Searcher
 */
package com.kuma.boot.ip2region.ip2region.support;

import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.ip2region.ip2region.doamin.IpInfo;
import org.lionsoul.ip2region.xdb.Searcher;

public class Ip2RegionSearch {
    private final Searcher searcher;

    public Ip2RegionSearch(Searcher searcher) {
        this.searcher = searcher;
    }

    public String searchAsString(String ip) {
        try {
            return this.searcher.search(ip);
        }
        catch (Exception e) {
            LogUtils.error((String)"Ip2Region Searcher fail! IP:{}", (Object[])new Object[]{ip});
            return "";
        }
    }

    public IpInfo searchAsInfo(String ip) {
        return IpInfo.of(this.searchAsString(ip));
    }

    public String searchAsJson(String ip) {
        return JacksonUtils.toJSONString((Object)this.searchAsInfo(ip));
    }
}

