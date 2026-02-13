/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.location;

import java.io.Serializable;

public class GeoPoint
implements Serializable {
    private static final long serialVersionUID = 3584864663880053897L;
    private double lon;
    private double lat;

    public GeoPoint offset(GeoPoint offset) {
        this.lon += offset.lon;
        this.lat += offset.lat;
        return this;
    }

    public GeoPoint(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public double getLon() {
        return this.lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return this.lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}

