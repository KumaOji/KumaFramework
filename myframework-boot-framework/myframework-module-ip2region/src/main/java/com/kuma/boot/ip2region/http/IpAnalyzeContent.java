/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.kuma.boot.ip2region.http;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class IpAnalyzeContent
implements Serializable {
    private String address;
    @JsonProperty(value="address_detail")
    private IpAnalyzeAddressDetail addressDetail;
    @JsonProperty(value="point")
    private IpAnalyzePoint point;

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public IpAnalyzeAddressDetail getAddressDetail() {
        return this.addressDetail;
    }

    public void setAddressDetail(IpAnalyzeAddressDetail addressDetail) {
        this.addressDetail = addressDetail;
    }

    public IpAnalyzePoint getPoint() {
        return this.point;
    }

    public void setPoint(IpAnalyzePoint point) {
        this.point = point;
    }
}

