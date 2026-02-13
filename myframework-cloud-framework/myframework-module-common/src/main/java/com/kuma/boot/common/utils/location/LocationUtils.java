/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.location;

import com.kuma.boot.common.utils.location.LocationIPO;
import java.util.ArrayList;
import java.util.List;

public class LocationUtils {
    private static final double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static double getDistance(LocationIPO locationIPO1, LocationIPO locationIPO2) {
        double lng1 = locationIPO1.getLng();
        double lat1 = locationIPO1.getLat();
        double lng2 = locationIPO2.getLng();
        double lat2 = locationIPO2.getLat();
        double radLat1 = LocationUtils.rad(lat1);
        double radLat2 = LocationUtils.rad(lat2);
        double a = radLat1 - radLat2;
        double b = LocationUtils.rad(lng1) - LocationUtils.rad(lng2);
        double s = 2.0 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2.0), 2.0) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2.0), 2.0)));
        s *= 6378.137;
        s = (double)Math.round(s * 10000.0) / 10000.0;
        return s *= 1000.0;
    }

    public static List<Double> getDistance(LocationIPO locationIPO, List<LocationIPO> locationIPOList) {
        ArrayList<Double> list = new ArrayList<Double>();
        for (LocationIPO location : locationIPOList) {
            list.add(LocationUtils.getDistance(locationIPO, location));
        }
        return list;
    }

    public static int getNearestLngAndLat(LocationIPO locationIPO, List<LocationIPO> locationIPOList) {
        int minIndex = 0;
        List<Double> list = LocationUtils.getDistance(locationIPO, locationIPOList);
        for (int i = 0; i < list.size(); ++i) {
            if (!(list.get(i) < list.get(minIndex))) continue;
            minIndex = i;
        }
        return minIndex;
    }
}

