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

package com.kuma.boot.core.utils.ip;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;

/** ip工具 */
public class IpHelper {

    /** qq lbs 地区查询key */
    @Value("${lili.lbs.key}")
    private String key;

    /** qq lbs 地区查询key */
    @Value("${lili.lbs.sk}")
    private String sk;

    private static final String API = "https://apis.map.qq.com";

    /**
     * 获取IP返回地理信息
     * @param request 请求参数
     * @return 城市信息
     */
    public String getIpCity(HttpServletRequest request) {

        // String url = "/ws/location/v1/ip?key=" + key + "&ip=" +
        // IpUtils.getIpAddress(request);
        // String sign = SecureUtil.md5(url + sk);
        // url = API + url + "&sign=" + sign;
        String result = "未知";
        // try {
        // String json = HttpUtil.get(url, 3000);
        // JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        // String status = jsonObject.get("status").getAsString();
        // if ("0".equals(status)) {
        // JsonObject address =
        // jsonObject.get("result").getAsJsonObject().get("ad_info").getAsJsonObject();
        // String nation = address.get("nation").getAsString();
        // String province = address.get("province").getAsString();
        // String city = address.get("city").getAsString();
        // String district = address.get("district").getAsString();
        // if (StrUtil.isNotBlank(nation) && StrUtil.isBlank(province)) {
        // result = nation;
        // } else {
        // result = province;
        // if (StrUtil.isNotBlank(city)) {
        // result += " " + city;
        // }
        // if (StrUtil.isNotBlank(district)) {
        // result += " " + district;
        // }
        // }
        // }
        // } catch (Exception e) {
        // log.info("获取IP地理信息失败");
        // }
        return result;
    }
}
