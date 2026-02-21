/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ip2region.http;

public class IpAnalyzeResponse {
    private String status;
    private Boolean successFlag;
    private String msg;
    private String province;
    private String city;
    private Double x;
    private Double y;

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getSuccessFlag() {
        return this.successFlag;
    }

    public void setSuccessFlag(Boolean successFlag) {
        this.successFlag = successFlag;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getX() {
        return this.x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return this.y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public static IpAnalyzeResponseBuilder builder() {
        return new IpAnalyzeResponseBuilder();
    }

    public static final class IpAnalyzeResponseBuilder {
        private IpAnalyzeResponse ipAnalyzeResponse = new IpAnalyzeResponse();

        private IpAnalyzeResponseBuilder() {
        }

        public IpAnalyzeResponseBuilder status(String status) {
            this.ipAnalyzeResponse.setStatus(status);
            return this;
        }

        public IpAnalyzeResponseBuilder successFlag(Boolean successFlag) {
            this.ipAnalyzeResponse.setSuccessFlag(successFlag);
            return this;
        }

        public IpAnalyzeResponseBuilder msg(String msg) {
            this.ipAnalyzeResponse.setMsg(msg);
            return this;
        }

        public IpAnalyzeResponseBuilder province(String province) {
            this.ipAnalyzeResponse.setProvince(province);
            return this;
        }

        public IpAnalyzeResponseBuilder city(String city) {
            this.ipAnalyzeResponse.setCity(city);
            return this;
        }

        public IpAnalyzeResponseBuilder x(Double x) {
            this.ipAnalyzeResponse.setX(x);
            return this;
        }

        public IpAnalyzeResponseBuilder y(Double y) {
            this.ipAnalyzeResponse.setY(y);
            return this;
        }

        public IpAnalyzeResponse build() {
            return this.ipAnalyzeResponse;
        }
    }
}

