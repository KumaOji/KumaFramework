/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.utils.common;

import cn.hutool.core.util.CharUtil;
import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.utils.lang.StringUtils;
import cn.hutool.core.util.StrUtil;

/**
 * 自动递增填充零
 *
 * @author kuma
 * @version 2022.06
 * @since 2022-07-27 20:23:33
 */
public class AutoIncrementZerofillUtils {

    /**
     * 获得初始化值，自动填充零
     * @param length 初始化长度
     * @return 如：0001
     */
    public static String getInitValue(int length) {
        return StrUtil.padPre("1", length, '0');
    }

    /**
     * 字符串尾部值自动递增
     * @param str 尾部值是 {@linkplain Integer} 类型
     * @return 自动递增后的值
     */
    public static String autoIncrement(String str) {
        int maxIndex = str.length() - 1;
        int autoIncrementValue = Integer.parseInt(CharUtil.toString(str.charAt(maxIndex))) + 1;
        if (autoIncrementValue == 10) {
            int cycleIndex = 0;
            for (int i = maxIndex - 1; i >= 0; i--) {
                int autoIncrementValueI = Integer.parseInt(CharUtil.toString(str.charAt(i))) + 1;
                cycleIndex++;
                if (autoIncrementValueI != 10) {
                    String pad = StrUtil.padPre("0", cycleIndex, '0');
                    String replaceValue = Integer.toString(autoIncrementValueI) + pad;
                    return StringUtils.replace(str, replaceValue, i, i + 1 + replaceValue.length());
                }
            }

            throw new BaseException("无法自动递增，此参数已是最大值：" + str);
        }

        return str.substring(0, maxIndex) + autoIncrementValue;
    }

    /**
     * 字符串尾部值自动递减
     * @param str 尾部值是 {@linkplain Integer} 类型
     * @return 自动递减后的值
     */
    public static String autoDecr(String str) {
        int maxIndex = str.length() - 1;
        int autoDecrValue = Integer.parseInt(CharUtil.toString(str.charAt(maxIndex))) - 1;
        if (autoDecrValue == -1) {
            int cycleIndex = 0;
            for (int i = maxIndex - 1; i >= 0; i--) {
                int autoDecrValueI = Integer.parseInt(CharUtil.toString(str.charAt(i))) - 1;
                cycleIndex++;
                if (autoDecrValueI != -1) {
                    String pad = StrUtil.padPre("9", cycleIndex, '9');
                    String replaceValue = Integer.toString(autoDecrValueI) + pad;
                    return StringUtils.replace(str, replaceValue, i, i + 1 + replaceValue.length());
                }
            }

            throw new BaseException("无法自动递减，此参数已是最小值：" + str);
        }

        return str.substring(0, maxIndex) + autoDecrValue;
    }
}
