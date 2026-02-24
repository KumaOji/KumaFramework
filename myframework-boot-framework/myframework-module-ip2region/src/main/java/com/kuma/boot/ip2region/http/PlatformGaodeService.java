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

import com.kuma.boot.common.utils.log.LogUtils;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
/**
 * @description 高德地图开放平台ip定位实现类
 */
public class PlatformGaodeService implements com.kuma.boot.ip2region.http.IPlatFormIpAnalyzeService {

    /**
     * 响应成功状态码
     */
    private final String OK_CODE = "1";

    @Value("${platform.conf.gaode.key}")
    private String key;

    @Override
    public com.kuma.boot.ip2region.http.IpAnalyzeResponse ipAnalyze(String ip) {
        String URL = "https://restapi.amap.com/v3/ip";
        Map<String, String> params = new HashMap<>();
        params.put("key", key);
        params.put("ip", ip);
        try {
            String json = SmartHttpUtil.sendGet(URL, params, null);
            com.kuma.boot.ip2region.http.IpAnalyzeGaodeResponse response = JSONUtil.toBean(json, com.kuma.boot.ip2region.http.IpAnalyzeGaodeResponse.class);
            if (OK_CODE.equals(response.getStatus())) {
                com.kuma.boot.ip2region.http.IpAnalyzeResponse ipAnalyzeResponse = com.kuma.boot.ip2region.http.IpAnalyzeResponse.builder()
                        .status(response.getInfocode())
                        .successFlag(true)
                        .msg(response.getInfo())
                        .province(response.getProvince())
                        .city(response.getCity())
                        .build();
                // 对rectangle进行解析
                String rectangle = response.getRectangle();
                String[] split = rectangle.split(";|,");
                if (split.length == 1) {
                    ipAnalyzeResponse.setSuccessFlag(false);
                    ipAnalyzeResponse.setStatus("0");
                    ipAnalyzeResponse.setMsg("ip[" + ip + "]非法");
                    return ipAnalyzeResponse;
                }
                ipAnalyzeResponse.setX((Double.parseDouble(split[0]) + Double.parseDouble(split[2])) / 2.0);
                ipAnalyzeResponse.setY((Double.parseDouble(split[1]) + Double.parseDouble(split[3])) / 2.0);
                return ipAnalyzeResponse;
            }
            return com.kuma.boot.ip2region.http.IpAnalyzeResponse.builder()
                    .status(response.getInfocode())
                    .successFlag(false)
                    .msg(response.getInfo())
                    .build();
        } catch (Exception e) {
            LogUtils.error(e);
            return com.kuma.boot.ip2region.http.IpAnalyzeResponse.builder()
                    .status("500")
                    .successFlag(false)
                    .msg(e.getMessage())
                    .build();
        }
    }
}
