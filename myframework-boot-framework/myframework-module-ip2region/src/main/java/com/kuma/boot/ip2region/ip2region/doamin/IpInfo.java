/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ip2region.ip2region.doamin;

public class IpInfo {
    private String nation;
    private String area;
    private String province;
    private String city;
    private String operator;

    private IpInfo(String info) {
        String[] arr = info.split("\\|");
        this.nation = this.checkData(arr[0]);
        this.area = this.checkData(arr[1]);
        this.province = this.checkData(arr[2]);
        this.city = this.checkData(arr[3]);
        this.operator = this.checkData(arr[4]);
    }

    public static IpInfo of(String info) {
        return new IpInfo(info);
    }

    private String checkData(String data) {
        return "0".equals(data) ? null : data;
    }

    public String getNation() {
        return this.nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getArea() {
        return this.area;
    }

    public void setArea(String area) {
        this.area = area;
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

    public String getOperator() {
        return this.operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}

