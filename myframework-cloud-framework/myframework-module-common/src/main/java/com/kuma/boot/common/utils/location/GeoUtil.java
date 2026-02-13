/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.location;

import com.kuma.boot.common.utils.location.GeoPoint;
import java.text.DecimalFormat;

public class GeoUtil {
    public static final double EARTH_RADIUS = 6378137.0;
    private static final double PI = Math.PI;
    private static final double X_PI = 52.35987755982988;
    public static final double RADIUS = 6378245.0;
    public static final double CORRECTION_PARAM = 0.006693421622965943;
    public static final DecimalFormat FORMAT = new DecimalFormat("#.########");

    public static double getDistance(double lon1, double lat1, double lon2, double lat2) {
        double radLat1 = GeoUtil.radian(lat1);
        double radLat2 = GeoUtil.radian(lat2);
        double a = radLat1 - radLat2;
        double b = GeoUtil.radian(lon1) - GeoUtil.radian(lon2);
        return 2.0 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2.0), 2.0) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2.0), 2.0))) * 6378137.0;
    }

    public static double getGpsValue(int coordinate) {
        int degrees = coordinate / 360000;
        int remainder = coordinate % 360000;
        int minutes = remainder / 6000;
        double seconds = (double)(remainder %= 6000) / 100.0;
        return (double)degrees + (double)minutes / 60.0 + seconds / 3600.0;
    }

    public static boolean isOutOfChina(double lng, double lat) {
        return lng < 72.004 || lng > 137.8347 || lat < 0.8293 || lat > 55.8271;
    }

    public static GeoPoint wgs84ToGcj02(double lon, double lat) {
        return new GeoPoint(lon, lat).offset(GeoUtil.offset(lon, lat, true));
    }

    public static GeoPoint wgs84ToBd09(double lon, double lat) {
        GeoPoint gcj02 = GeoUtil.wgs84ToGcj02(lon, lat);
        return GeoUtil.gcj02ToBd09(gcj02.getLon(), gcj02.getLat());
    }

    public static GeoPoint gcj02ToWgs84(double lon, double lat) {
        return new GeoPoint(lon, lat).offset(GeoUtil.offset(lon, lat, false));
    }

    public static GeoPoint gcj02ToBd09(double lon, double lat) {
        double z = Math.sqrt(lon * lon + lat * lat) + 2.0E-5 * Math.sin(lat * 52.35987755982988);
        double theta = Math.atan2(lat, lon) + 3.0E-6 * Math.cos(lon * 52.35987755982988);
        double bd_lng = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return new GeoPoint(bd_lng, bd_lat);
    }

    public static GeoPoint bd09ToGcj02(double lon, double lat) {
        double x = lon - 0.0065;
        double y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 2.0E-5 * Math.sin(y * 52.35987755982988);
        double theta = Math.atan2(y, x) - 3.0E-6 * Math.cos(x * 52.35987755982988);
        double gg_lon = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new GeoPoint(gg_lon, gg_lat);
    }

    public static GeoPoint bd09toWgs84(double lon, double lat) {
        GeoPoint gcj02 = GeoUtil.bd09ToGcj02(lon, lat);
        return GeoUtil.gcj02ToWgs84(gcj02.getLon(), gcj02.getLat());
    }

    public static GeoPoint wgs84ToMercator(double lon, double lat) {
        double x = lon * 2.0037508342789244E7 / 180.0;
        double y = Math.log(Math.tan((90.0 + lat) * Math.PI / 360.0)) / (Math.PI / 180);
        y = y * 2.0037508342789244E7 / 180.0;
        return new GeoPoint(x, y);
    }

    public static GeoPoint mercatorToWgs84(double mercatorX, double mercatorY) {
        double x = mercatorX / 2.0037508342789244E7 * 180.0;
        double y = mercatorY / 2.0037508342789244E7 * 180.0;
        y = 57.29577951308232 * (2.0 * Math.atan(Math.exp(y * Math.PI / 180.0)) - 1.5707963267948966);
        return new GeoPoint(x, y);
    }

    public static String formatGeo(double value) {
        return FORMAT.format(value);
    }

    private static GeoPoint offset(double lon, double lat, boolean isPlus) {
        double dlon = GeoUtil.transLon(lon - 105.0, lat - 35.0);
        double dlat = GeoUtil.transLat(lon - 105.0, lat - 35.0);
        double magic = Math.sin(lat / 180.0 * Math.PI);
        magic = 1.0 - 0.006693421622965943 * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dlon = dlon * 180.0 / (6378245.0 / sqrtMagic * Math.cos(lat / 180.0 * Math.PI) * Math.PI);
        dlat = dlat * 180.0 / (6335552.717000426 / (magic * sqrtMagic) * Math.PI);
        if (isPlus) {
            return new GeoPoint(dlon, dlat);
        }
        return new GeoPoint(-dlon, -dlat);
    }

    private static double transLon(double lon, double lat) {
        double ret = 300.0 + lon + 2.0 * lat + 0.1 * lon * lon + 0.1 * lon * lat + 0.1 * Math.sqrt(Math.abs(lon));
        ret += (20.0 * Math.sin(6.0 * lon * Math.PI) + 20.0 * Math.sin(2.0 * lon * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lon * Math.PI) + 40.0 * Math.sin(lon / 3.0 * Math.PI)) * 2.0 / 3.0;
        return ret += (150.0 * Math.sin(lon / 12.0 * Math.PI) + 300.0 * Math.sin(lon / 30.0 * Math.PI)) * 2.0 / 3.0;
    }

    private static double transLat(double lon, double lat) {
        double ret = -100.0 + 2.0 * lon + 3.0 * lat + 0.2 * lat * lat + 0.1 * lon * lat + 0.2 * Math.sqrt(Math.abs(lon));
        ret += (20.0 * Math.sin(6.0 * lon * Math.PI) + 20.0 * Math.sin(2.0 * lon * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * Math.PI) + 40.0 * Math.sin(lat / 3.0 * Math.PI)) * 2.0 / 3.0;
        return ret += (160.0 * Math.sin(lat / 12.0 * Math.PI) + 320.0 * Math.sin(lat * Math.PI / 30.0)) * 2.0 / 3.0;
    }

    private static double radian(double d) {
        return d * Math.PI / 180.0;
    }
}

