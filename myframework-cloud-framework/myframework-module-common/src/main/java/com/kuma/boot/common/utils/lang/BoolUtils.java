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

package com.kuma.boot.common.utils.lang;

/** 布尔值工具类 */
public final class BoolUtils {

    private BoolUtils() {}

    /** Y 字符串 */
    public static final String Y = "Y";

    /** N 字符串 */
    public static final String N = "N";

    /**
     * 获取对应的bool值
     * @param boolStr 布尔字符串
     * @return 是否
     */
    public static boolean getBool(String boolStr) {
        if ("YES".equals(boolStr)) {
            return true;
        }
        if ("Y".equals(boolStr)) {
            return true;
        }
        if ("1".equals(boolStr)) {
            return true;
        }
        if ("true".equals(boolStr)) {
            return true;
        }
        if ("是".equals(boolStr)) {
            return true;
        }

        return false;
    }

    /**
     * 结果
     * @param value 布尔值
     * @return 结果
     */
    public static String getYesOrNo(boolean value) {
        if (value) {
            return BoolUtils.Y;
        }

        return BoolUtils.N;
    }
}
