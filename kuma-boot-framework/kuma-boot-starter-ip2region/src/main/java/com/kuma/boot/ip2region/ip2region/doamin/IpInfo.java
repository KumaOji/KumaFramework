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

package com.kuma.boot.ip2region.ip2region.doamin;

/**
 * <p>
 * IpInfo
 * </p>
 *
 *
 */
public class IpInfo {

    private String nation;

    private String area;

    private String province;

    private String city;

    private String operator;

    private IpInfo(String info) {
        String[] arr = info.split("\\|");
        nation = checkData(arr[0]);
        area = checkData(arr[1]);
        province = checkData(arr[2]);
        city = checkData(arr[3]);
        operator = checkData(arr[4]);
    }

    /**
     * Of ip info.
     *
     * @param info the info
     * @return the ip info
     */
    public static IpInfo of(String info) {
        return new IpInfo(info);
    }

    private String checkData(String data) {
        return "0".equals(data) ? null : data;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
