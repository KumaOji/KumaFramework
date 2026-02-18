/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.utils.location;

import java.io.Serializable;

/**
 * 坐标点
 *
 * @author JourWon、hutool、L.cm
 */
public class GeoPoint implements Serializable {

    private static final long serialVersionUID = 3584864663880053897L;

    /**
     * 经度
     */
    private double lon;

    /**
     * 纬度
     */
    private double lat;

    /**
     * 当前坐标偏移指定坐标
     * @param offset 偏移量
     * @return this
     */
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
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
