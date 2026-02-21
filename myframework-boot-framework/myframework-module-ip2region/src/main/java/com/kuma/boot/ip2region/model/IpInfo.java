/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.collection.CollUtil
 */
package com.kuma.boot.ip2region.model;

import cn.hutool.core.collection.CollUtil;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;

public class IpInfo
implements Serializable {
    private String country;
    private String region;
    private String province;
    private String city;
    private String isp;

    public String getAddress() {
        LinkedHashSet<String> regionSet = new LinkedHashSet<String>();
        regionSet.add(this.country);
        regionSet.add(this.region);
        regionSet.add(this.province);
        regionSet.add(this.city);
        regionSet.removeIf(Objects::isNull);
        return CollUtil.join(regionSet, (CharSequence)"");
    }

    public String getAddressAndIsp() {
        LinkedHashSet<String> regionSet = new LinkedHashSet<String>();
        regionSet.add(this.country);
        regionSet.add(this.region);
        regionSet.add(this.province);
        regionSet.add(this.city);
        regionSet.add(this.isp);
        regionSet.removeIf(Objects::isNull);
        return CollUtil.join(regionSet, (CharSequence)" ");
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return this.region;
    }

    public void setRegion(String region) {
        this.region = region;
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

    public String getIsp() {
        return this.isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }
}

