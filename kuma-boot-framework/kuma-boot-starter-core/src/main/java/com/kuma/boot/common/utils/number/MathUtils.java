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

package com.kuma.boot.common.utils.number;

import java.util.List;

/** 数学工具类 */
public final class MathUtils {

    private MathUtils() {}

    /** 黄金分割比 */
    public static final double GOLD_SECTION = 0.61803398874989484820458683436565;

    /**
     * 辗转相除法的递归调用求两个数的最大公约数
     * @param x 其中一个数
     * @param y 其中另一个数
     * @return 递归调用，最终返回最大公约数
     */
    public static int gcd(int x, int y) {
        return y == 0 ? x : gcd(y, x % y);
    }

    /**
     * 求n个数的最大公约数
     * @param list 列表
     * @return 递归调用，最终返回最大公约数
     */
    public static int ngcd(List<Integer> list) {
        return ngcd(list, list.size());
    }

    /**
     * 求n个数的最大公约数
     * @param target n个数的集合
     * @param z 数据个数
     * @return 递归调用，最终返回最大公约数
     */
    private static int ngcd(List<Integer> target, int z) {
        if (z == 1) {
            // 真正返回的最大公约数
            return target.get(0);
        }
        // 递归调用，两个数两个数的求
        return gcd(target.get(z - 1), ngcd(target, z - 1));
    }

    /**
     * 辗转相除法的递归调用求两个数的最小公倍数
     * @param x 其中一个数
     * @param y 其中另一个数
     * @return 递归调用，最终返回最小公倍数
     */
    public static int lcm(int x, int y) {
        return (x * y) / gcd(x, y);
    }

    /**
     * 求n个数的最小公倍数
     * @param list list n个数的集合
     * @return 递归调用，最终返回最小公倍数
     */
    public static int nlcm(List<Integer> list) {
        return nlcm(list, list.size());
    }

    /**
     * 求n个数的最小公倍数
     * @param target list n个数的集合
     * @param z 数据个数
     * @return 递归调用，最终返回最小公倍数
     */
    private static int nlcm(List<Integer> target, int z) {
        if (z == 1) {
            // 真正返回的最小公倍数
            return target.get(z - 1);
        }
        // 递归调用，两个数两个数的求
        return lcm(target.get(z - 1), nlcm(target, z - 1));
    }
}
