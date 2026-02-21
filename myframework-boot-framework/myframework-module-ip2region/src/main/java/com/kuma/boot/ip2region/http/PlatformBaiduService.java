/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.json.JSONUtil
 *  org.springframework.beans.factory.annotation.Value
 */
package com.kuma.boot.ip2region.http;

import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import org.springframework.beans.factory.annotation.Value;

public class PlatformBaiduService
implements IPlatFormIpAnalyzeService {
    private final Integer OK_CODE = 0;
    @Value(value="${platform.conf.baidu.ak}")
    public String ak;

    @Override
    public IpAnalyzeResponse ipAnalyze(String ip) {
        String URL2 = "http://api.map.baidu.com/location/ip";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ip", ip);
        params.put("ak", this.ak);
        params.put("coor", "bd09ll");
        try {
            String json = SmartHttpUtil.sendGet(URL2, params, null);
            IpAnalyzeBaiduResponse response = (IpAnalyzeBaiduResponse)JSONUtil.toBean((String)json, IpAnalyzeBaiduResponse.class);
            if (this.OK_CODE.equals(response.getStatus())) {
                return IpAnalyzeResponse.builder().status("" + response.getStatus()).successFlag(true).msg(response.getMessage()).province(response.getContent().getAddressDetail().getProvince()).city(response.getContent().getAddressDetail().getCity()).x(Double.parseDouble(response.getContent().getPoint().getX())).y(Double.parseDouble(response.getContent().getPoint().getY())).build();
            }
            return IpAnalyzeResponse.builder().status("" + response.getStatus()).successFlag(false).msg(response.getMessage()).build();
        }
        catch (Exception e) {
            return IpAnalyzeResponse.builder().status("500").successFlag(false).msg(e.getMessage()).build();
        }
    }
}

