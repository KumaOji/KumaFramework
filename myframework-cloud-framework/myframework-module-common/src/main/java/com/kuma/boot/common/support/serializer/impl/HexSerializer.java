/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.DecoderException
 *  org.apache.commons.codec.binary.Hex
 */
package com.kuma.boot.common.support.serializer.impl;

import com.kuma.boot.common.support.serializer.Serializer;
import com.kuma.boot.common.utils.log.LogUtils;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class HexSerializer
implements Serializer {
    @Override
    public String name() {
        return "hex";
    }

    @Override
    public byte[] serialize(Object o) {
        if (o == null) {
            return new byte[0];
        }
        if (o instanceof String) {
            String source = (String)o;
            char[] chars = source.toCharArray();
            try {
                return Hex.decodeHex((char[])chars);
            }
            catch (DecoderException e) {
                return new byte[0];
            }
        }
        LogUtils.error("hex \u53ea\u652f\u6301\u5b57\u7b26\u4e32\u5e8f\u5217\u5316 ", new Object[0]);
        return new byte[0];
    }

    @Override
    public Object deserialize(byte[] bytes, ClassLoader classLoader) {
        return new String(Hex.encodeHex((byte[])bytes));
    }
}

