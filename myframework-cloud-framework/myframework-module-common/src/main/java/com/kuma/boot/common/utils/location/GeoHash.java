/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeoHash {
    public static final double MINLAT = -90.0;
    public static final double MAXLAT = 90.0;
    public static final double MINLNG = -180.0;
    public static final double MAXLNG = 180.0;
    private static final char[] BASE32CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private int hashLength = 8;
    private int latLength = 20;
    private int lngLength = 20;
    private double minLat;
    private double minLng;
    private double lat;
    private double lng;

    public GeoHash(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
        this.setMinLatLng();
    }

    private void setMinLatLng() {
        int i;
        this.minLat = 180.0;
        for (i = 0; i < this.latLength; ++i) {
            this.minLat /= 2.0;
        }
        this.minLng = 360.0;
        for (i = 0; i < this.lngLength; ++i) {
            this.minLng /= 2.0;
        }
    }

    public void setHashLength(int length) {
        if (length > 1) {
            this.hashLength = length;
            this.latLength = length * 5 / 2;
            this.lngLength = length % 2 == 0 ? this.latLength : this.latLength + 1;
            this.setMinLatLng();
        }
    }

    private boolean[] getHashArray(double value, double min, double max, int length) {
        if (value < min || value > max) {
            return null;
        }
        if (length < 1) {
            return null;
        }
        boolean[] result = new boolean[length];
        for (int i = 0; i < length; ++i) {
            double mid = (min + max) / 2.0;
            if (value > mid) {
                result[i] = true;
                min = mid;
                continue;
            }
            result[i] = false;
            max = mid;
        }
        return result;
    }

    private boolean[] getGeoBinary(double lat, double lng) {
        boolean[] latArray = this.getHashArray(lat, -90.0, 90.0, this.latLength);
        boolean[] lngArray = this.getHashArray(lng, -180.0, 180.0, this.lngLength);
        return this.merge(latArray, lngArray);
    }

    private boolean[] merge(boolean[] latArray, boolean[] lngArray) {
        int i;
        if (latArray == null || lngArray == null) {
            return null;
        }
        boolean[] result = new boolean[lngArray.length + latArray.length];
        Arrays.fill(result, false);
        for (i = 0; i < lngArray.length; ++i) {
            result[2 * i] = lngArray[i];
        }
        for (i = 0; i < latArray.length; ++i) {
            result[2 * i + 1] = latArray[i];
        }
        return result;
    }

    private String getGeoHashBase32(double lat, double lng) {
        boolean[] bools = this.getGeoBinary(lat, lng);
        if (bools == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bools.length; i += 5) {
            boolean[] base32 = new boolean[5];
            for (int j = 0; j < 5; ++j) {
                base32[j] = bools[i + j];
            }
            char cha = this.getBase32Char(base32);
            if (' ' == cha) {
                return null;
            }
            sb.append(cha);
        }
        return sb.toString();
    }

    private char getBase32Char(boolean[] base32) {
        if (base32 == null || base32.length != 5) {
            return ' ';
        }
        int num = 0;
        for (boolean bool : base32) {
            num <<= 1;
            if (!bool) continue;
            ++num;
        }
        return BASE32CHARS[num % BASE32CHARS.length];
    }

    public List<String> getGeoHashBase32ForAround() {
        String rightDown;
        String rightMid;
        String rightUp;
        String midDown;
        String midMid;
        String midUp;
        String leftDown;
        String leftMid;
        double leftLat = this.lat - this.minLat;
        double rightLat = this.lat + this.minLat;
        double upLng = this.lng - this.minLng;
        double downLng = this.lng + this.minLng;
        ArrayList<String> base32ForAround = new ArrayList<String>();
        String leftUp = this.getGeoHashBase32(leftLat, upLng);
        if (leftUp != null && !"".equals(leftUp)) {
            base32ForAround.add(leftUp);
        }
        if ((leftMid = this.getGeoHashBase32(leftLat, this.lng)) != null && !"".equals(leftMid)) {
            base32ForAround.add(leftMid);
        }
        if ((leftDown = this.getGeoHashBase32(leftLat, downLng)) != null && !"".equals(leftDown)) {
            base32ForAround.add(leftDown);
        }
        if ((midUp = this.getGeoHashBase32(this.lat, upLng)) != null && !"".equals(midUp)) {
            base32ForAround.add(midUp);
        }
        if ((midMid = this.getGeoHashBase32(this.lat, this.lng)) != null && !"".equals(midMid)) {
            base32ForAround.add(midMid);
        }
        if ((midDown = this.getGeoHashBase32(this.lat, downLng)) != null && !"".equals(midDown)) {
            base32ForAround.add(midDown);
        }
        if ((rightUp = this.getGeoHashBase32(rightLat, upLng)) != null && !"".equals(rightUp)) {
            base32ForAround.add(rightUp);
        }
        if ((rightMid = this.getGeoHashBase32(rightLat, this.lng)) != null && !"".equals(rightMid)) {
            base32ForAround.add(rightMid);
        }
        if ((rightDown = this.getGeoHashBase32(rightLat, downLng)) != null && !"".equals(rightDown)) {
            base32ForAround.add(rightDown);
        }
        return base32ForAround;
    }

    public String getGeoHashBase32() {
        return this.getGeoHashBase32(this.lat, this.lng);
    }
}

