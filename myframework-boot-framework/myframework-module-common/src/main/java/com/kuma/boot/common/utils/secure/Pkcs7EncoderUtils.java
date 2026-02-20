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

package com.kuma.boot.common.utils.secure;

import java.util.Arrays;

/**
 * 提供基于 PKCS7 算法的加解密接口.
 *
 * <p>
 * 参考自：jFinal 方便使用
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 19:41:13
 */
public class Pkcs7EncoderUtils {

    /** 默认为 16，保持跟其他语言的一致性 */
    private static final int BLOCK_SIZE = 16;

    /**
     * PKCS7 编码 padding 补位
     * @param src 原数据
     * @return padding 补位
     */
    public static byte[] encode(byte[] src) {
        int count = src.length;
        // 计算需要填充的位数
        int amountToPad = BLOCK_SIZE - (count % BLOCK_SIZE);
        // 获得补位所用的字符
        byte pad = (byte) (amountToPad & 0xFF);
        byte[] pads = new byte[amountToPad];

        Arrays.fill(pads, pad);

        int length = count + amountToPad;
        byte[] dest = new byte[length];
        System.arraycopy(src, 0, dest, 0, count);
        System.arraycopy(pads, 0, dest, count, amountToPad);
        return dest;
    }

    /**
     * PKCS7 解码
     * @param decrypted 编码的数据
     * @return 解码后的数据
     */
    public static byte[] decode(byte[] decrypted) {
        int pad = decrypted[decrypted.length - 1];
        if (pad < 1 || pad > BLOCK_SIZE) {
            pad = 0;
        }
        if (pad > 0) {
            return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
        }
        return decrypted;
    }
}
