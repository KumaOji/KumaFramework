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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 */
public class IpAnalyzeContent implements Serializable {

    /**
     * 简要地址
     */
    private String address;

    /**
     * 详细地址信息
     */
    @JsonProperty("address_detail")
    private com.kuma.boot.ip2region.http.IpAnalyzeAddressDetail addressDetail;

    /**
     *  百度经纬度坐标值
     */
    @JsonProperty("point")
    private IpAnalyzePoint point;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public com.kuma.boot.ip2region.http.IpAnalyzeAddressDetail getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(com.kuma.boot.ip2region.http.IpAnalyzeAddressDetail addressDetail) {
        this.addressDetail = addressDetail;
    }

    public IpAnalyzePoint getPoint() {
        return point;
    }

    public void setPoint(IpAnalyzePoint point) {
        this.point = point;
    }
}
