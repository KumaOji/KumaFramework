/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

/**
 * 位置工具类
 *
 * <p>
 * 源码来自：https://v2ex.com/t/661809
 *
 * <p>
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 19:41:13
 */
public class GeoUtils {

    /** 地球的半径 (m) */
    public static final double EARTH_RADIUS = 6378137;

    /**
     * 根据经纬度计算两点之间的距离 (m)
     * @param lng1 位置 1 的经度
     * @param lat1 位置 1 的纬度
     * @param lng2 位置 2 的经度
     * @param lat2 位置 2 的纬度
     * @return 返回距离
     */
    public static double getDistance(double lng1, double lat1, double lng2, double lat2) {
        double radLat1 = radian(lat1);
        double radLat2 = radian(lat2);
        double a = radLat1 - radLat2;
        double b = radian(lng1) - radian(lng2);
        return (2
                * Math.asin(
                Math.sqrt(
                        Math.pow(Math.sin(a / 2), 2)
                                + Math.cos(radLat1)
                                * Math.cos(radLat2)
                                * Math.pow(Math.sin(b / 2), 2))))
                * EARTH_RADIUS;
    }

    private static double radian(double d) {
        return d * Math.PI / 180.0;
    }
}
