/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.json.JSONUtil
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.beans.factory.annotation.Value
 */
package com.kuma.boot.ip2region.http;

import cn.hutool.json.JSONUtil;
import com.kuma.boot.common.utils.log.LogUtils;

import java.util.HashMap;
import org.springframework.beans.factory.annotation.Value;

public class PlatformGaodeService
implements IPlatFormIpAnalyzeService {
    private final String OK_CODE = "1";
    @Value(value="${platform.conf.gaode.key}")
    private String key;

    @Override
    public IpAnalyzeResponse ipAnalyze(String ip) {
        String URL2 = "https://restapi.amap.com/v3/ip";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", this.key);
        params.put("ip", ip);
        try {
            String json = SmartHttpUtil.sendGet(URL2, params, null);
            IpAnalyzeGaodeResponse response = (IpAnalyzeGaodeResponse)JSONUtil.toBean((String)json, IpAnalyzeGaodeResponse.class);
            if ("1".equals(response.getStatus())) {
                IpAnalyzeResponse ipAnalyzeResponse = IpAnalyzeResponse.builder().status(response.getInfocode()).successFlag(true).msg(response.getInfo()).province(response.getProvince()).city(response.getCity()).build();
                String rectangle = response.getRectangle();
                String[] split = rectangle.split(";|,");
                if (split.length == 1) {
                    ipAnalyzeResponse.setSuccessFlag(false);
                    ipAnalyzeResponse.setStatus("0");
                    ipAnalyzeResponse.setMsg("ip[" + ip + "]\u975e\u6cd5");
                    return ipAnalyzeResponse;
                }
                ipAnalyzeResponse.setX((Double.parseDouble(split[0]) + Double.parseDouble(split[2])) / 2.0);
                ipAnalyzeResponse.setY((Double.parseDouble(split[1]) + Double.parseDouble(split[3])) / 2.0);
                return ipAnalyzeResponse;
            }
            return IpAnalyzeResponse.builder().status(response.getInfocode()).successFlag(false).msg(response.getInfo()).build();
        }
        catch (Exception e) {
            LogUtils.error((Throwable)e);
            return IpAnalyzeResponse.builder().status("500").successFlag(false).msg(e.getMessage()).build();
        }
    }
}

