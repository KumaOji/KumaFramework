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

package com.kuma.boot.common.utils.common;

import java.util.Arrays;
import com.kuma.boot.common.utils.lang.StringUtils;

/**
 * 掩码脱敏工具类
 *
 */
public final class MaskUtils {

    private MaskUtils() {}

    /**
     * 打码
     * @param source 原始串
     * @param frontRetain 开头保留多少位
     * @param rearRetain 结尾保留多少位
     * @param masker 掩码
     * @return 打码后的字符串
     */
    public static String mask(String source, int frontRetain, int rearRetain, char masker) {

        if (StringUtils.isEmpty(source)) {
            return source;
        }

        // 源字符串长度
        int len = source.length();

        // 需要打码的长度 = 总长度 - 总保留
        int maskLength = len - (frontRetain + rearRetain);

        // 不需要打码
        if (maskLength <= 0) {
            return source;
        }

        // 掩码数组
        char[] masks = new char[maskLength];
        Arrays.fill(masks, masker);

        // 原始数组
        char[] chars = source.toCharArray();

        // 将掩码数组拷贝到原始数组对应位上
        System.arraycopy(masks, 0, chars, frontRetain, maskLength);

        return new String(chars);
    }
}
