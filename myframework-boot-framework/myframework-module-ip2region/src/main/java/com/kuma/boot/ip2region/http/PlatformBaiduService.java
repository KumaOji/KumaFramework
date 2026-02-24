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

package com.kuma.boot.ip2region.http;

import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 *  百度地图开发平台实现类
 */
public class PlatformBaiduService implements com.kuma.boot.ip2region.http.IPlatFormIpAnalyzeService {

    /**
     * 响应成功状态码
     */
    private final Integer OK_CODE = 0;

    @Value("${platform.conf.baidu.ak}")
    public String ak;

    @Override
    public com.kuma.boot.ip2region.http.IpAnalyzeResponse ipAnalyze(String ip) {
        String URL = "http://api.map.baidu.com/location/ip";
        Map<String, String> params = new HashMap<>();
        params.put("ip", ip);
        params.put("ak", ak);
        params.put("coor", "bd09ll");
        try {
            String json = SmartHttpUtil.sendGet(URL, params, null);
            com.kuma.boot.ip2region.http.IpAnalyzeBaiduResponse response = JSONUtil.toBean(json, com.kuma.boot.ip2region.http.IpAnalyzeBaiduResponse.class);
            if (OK_CODE.equals(response.getStatus())) {
                return com.kuma.boot.ip2region.http.IpAnalyzeResponse.builder()
                        .status(response.getStatus() + "")
                        .successFlag(true)
                        .msg(response.getMessage())
                        .province(response.getContent().getAddressDetail().getProvince())
                        .city(response.getContent().getAddressDetail().getCity())
                        .x(Double.parseDouble(response.getContent().getPoint().getX()))
                        .y(Double.parseDouble(response.getContent().getPoint().getY()))
                        .build();
            }
            return com.kuma.boot.ip2region.http.IpAnalyzeResponse.builder()
                    .status(response.getStatus() + "")
                    .successFlag(false)
                    .msg(response.getMessage())
                    .build();
        } catch (Exception e) {
            return com.kuma.boot.ip2region.http.IpAnalyzeResponse.builder()
                    .status("500")
                    .successFlag(false)
                    .msg(e.getMessage())
                    .build();
        }
    }
}
