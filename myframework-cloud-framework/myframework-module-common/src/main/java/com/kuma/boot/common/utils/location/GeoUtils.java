/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.location;

public class GeoUtils {
    public static final double EARTH_RADIUS = 6378137.0;

    public static double getDistance(double lng1, double lat1, double lng2, double lat2) {
        double radLat1 = GeoUtils.radian(lat1);
        double radLat2 = GeoUtils.radian(lat2);
        double a = radLat1 - radLat2;
        double b = GeoUtils.radian(lng1) - GeoUtils.radian(lng2);
        return 2.0 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2.0), 2.0) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2.0), 2.0))) * 6378137.0;
    }

    private static double radian(double d) {
        return d * Math.PI / 180.0;
    }
}

