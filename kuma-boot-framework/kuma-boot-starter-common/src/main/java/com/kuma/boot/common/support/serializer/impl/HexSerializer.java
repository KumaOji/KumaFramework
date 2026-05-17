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

package com.kuma.boot.common.support.serializer.impl;

import com.kuma.boot.common.support.serializer.Serializer;
import com.kuma.boot.common.support.serializer.SerializerConstants;
import com.kuma.boot.common.utils.log.LogUtils;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * HexSerializer
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class HexSerializer implements Serializer {

    @Override
    public String name() {
        return SerializerConstants.HEX;
    }

    @Override
    public byte[] serialize( Object o ) {
        if (o == null) {
            return new byte[0];
        }
        // 只能转换字符串
        if (o instanceof String source) {
            char[] chars = source.toCharArray();
            try {
                return Hex.decodeHex(chars);
            } catch (DecoderException e) {
                return new byte[0];
            }
        }
        LogUtils.error("hex 只支持字符串序列化 ");
        return new byte[0];
    }

    @Override
    public Object deserialize( byte[] bytes, ClassLoader classLoader ) {
        return new String(Hex.encodeHex(bytes));
    }
}
