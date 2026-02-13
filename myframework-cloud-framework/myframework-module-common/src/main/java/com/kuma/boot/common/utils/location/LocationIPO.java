/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson2.JSONObject
 */
package com.kuma.boot.common.utils.location;

import com.alibaba.fastjson2.JSONObject;

public class LocationIPO {
    private double lng;
    private double lat;

    public static LocationIPO toLocationIPO(JSONObject location) {
        double lng = location.getDouble("lng");
        double lat = location.getDouble("lat");
        return LocationIPO.builder().lng(lng).lat(lat).build();
    }

    public LocationIPO() {
    }

    public LocationIPO(double lng, double lat) {
        this.lng = lng;
        this.lat = lat;
    }

    public double getLng() {
        return this.lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return this.lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public static LocationIPOBuilder builder() {
        return new LocationIPOBuilder();
    }

    public static final class LocationIPOBuilder {
        private double lng;
        private double lat;

        private LocationIPOBuilder() {
        }

        public LocationIPOBuilder lng(double lng) {
            this.lng = lng;
            return this;
        }

        public LocationIPOBuilder lat(double lat) {
            this.lat = lat;
            return this;
        }

        public LocationIPO build() {
            LocationIPO locationIPO = new LocationIPO();
            locationIPO.setLng(this.lng);
            locationIPO.setLat(this.lat);
            return locationIPO;
        }
    }
}

