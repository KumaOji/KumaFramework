/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.kuma.boot.ip2region.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class IpAnalyzeAddressDetail
implements Serializable {
    private String city;
    @JsonProperty(value="city_code")
    private Integer cityCode;
    private String district;
    private String province;
    private String street;
    @JsonProperty(value="street_number")
    private String streetNumber;

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getCityCode() {
        return this.cityCode;
    }

    public void setCityCode(Integer cityCode) {
        this.cityCode = cityCode;
    }

    public String getDistrict() {
        return this.district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return this.streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }
}

