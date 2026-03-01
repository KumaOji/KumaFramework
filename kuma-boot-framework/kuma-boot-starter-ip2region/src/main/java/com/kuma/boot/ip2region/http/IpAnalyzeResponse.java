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

public class IpAnalyzeResponse {

    /**
     * 状态码
     */
    private String status;

    /**
     * 是否成功
     */
    private Boolean successFlag;

    /**
     * 失败原因
     */
    private String msg;

    /**
     * 省份名称
     */
    private String province;

    /**
     * 城市名称
     */
    private String city;

    /**
     * 经度
     */
    private Double x;

    /**
     * 维度
     */
    private Double y;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getSuccessFlag() {
        return successFlag;
    }

    public void setSuccessFlag(Boolean successFlag) {
        this.successFlag = successFlag;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public static IpAnalyzeResponseBuilder builder() {
        return new IpAnalyzeResponseBuilder();
    }

    public static final class IpAnalyzeResponseBuilder {
        private IpAnalyzeResponse ipAnalyzeResponse;

        private IpAnalyzeResponseBuilder() {
            ipAnalyzeResponse = new IpAnalyzeResponse();
        }

        public IpAnalyzeResponseBuilder status(String status) {
            ipAnalyzeResponse.setStatus(status);
            return this;
        }

        public IpAnalyzeResponseBuilder successFlag(Boolean successFlag) {
            ipAnalyzeResponse.setSuccessFlag(successFlag);
            return this;
        }

        public IpAnalyzeResponseBuilder msg(String msg) {
            ipAnalyzeResponse.setMsg(msg);
            return this;
        }

        public IpAnalyzeResponseBuilder province(String province) {
            ipAnalyzeResponse.setProvince(province);
            return this;
        }

        public IpAnalyzeResponseBuilder city(String city) {
            ipAnalyzeResponse.setCity(city);
            return this;
        }

        public IpAnalyzeResponseBuilder x(Double x) {
            ipAnalyzeResponse.setX(x);
            return this;
        }

        public IpAnalyzeResponseBuilder y(Double y) {
            ipAnalyzeResponse.setY(y);
            return this;
        }

        public IpAnalyzeResponse build() {
            return ipAnalyzeResponse;
        }
    }
}
