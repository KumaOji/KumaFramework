/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.location;

import com.kuma.boot.common.utils.location.GeoPoint;
import com.kuma.boot.common.utils.location.GeoUtil;

public enum GeoType {
    WGS84("WGS84", "\u5730\u7403\u5750\u6807\u7cfb\uff0c\u56fd\u9645\u901a\u7528\u5750\u6807\u7cfb"){

        @Override
        public GeoPoint toWGS84(double lon, double lat) {
            return new GeoPoint(lon, lat);
        }

        @Override
        public GeoPoint toGCJ02(double lon, double lat) {
            return GeoUtil.wgs84ToGcj02(lon, lat);
        }

        @Override
        public GeoPoint toBD09(double lon, double lat) {
            return GeoUtil.wgs84ToBd09(lon, lat);
        }
    }
    ,
    GCJ02("GCJ02", "\u706b\u661f\u5750\u6807\u7cfb\uff0c\u9ad8\u5fb7\u3001\u817e\u8baf\u3001\u963f\u91cc\u7b49\u4f7f\u7528"){

        @Override
        public GeoPoint toWGS84(double lon, double lat) {
            return GeoUtil.gcj02ToWgs84(lon, lat);
        }

        @Override
        public GeoPoint toGCJ02(double lon, double lat) {
            return new GeoPoint(lon, lat);
        }

        @Override
        public GeoPoint toBD09(double lon, double lat) {
            return GeoUtil.gcj02ToBd09(lon, lat);
        }
    }
    ,
    BD09("BD09", "\u767e\u5ea6\u5750\u6807\u7cfb\uff0c\u767e\u5ea6\u3001\u641c\u72d7\u7b49\u4f7f\u7528"){

        @Override
        public GeoPoint toWGS84(double lon, double lat) {
            return GeoUtil.bd09toWgs84(lon, lat);
        }

        @Override
        public GeoPoint toGCJ02(double lon, double lat) {
            return GeoUtil.bd09ToGcj02(lon, lat);
        }

        @Override
        public GeoPoint toBD09(double lon, double lat) {
            return new GeoPoint(lon, lat);
        }
    };

    private final String type;
    private final String desc;

    private GeoType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public abstract GeoPoint toWGS84(double var1, double var3);

    public abstract GeoPoint toGCJ02(double var1, double var3);

    public abstract GeoPoint toBD09(double var1, double var3);

    public String getType() {
        return this.type;
    }

    public String getDesc() {
        return this.desc;
    }
}

