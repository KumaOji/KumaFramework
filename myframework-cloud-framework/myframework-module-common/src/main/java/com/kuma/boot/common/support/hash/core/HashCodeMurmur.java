/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.hash.core;

import com.kuma.boot.common.support.hash.core.AbstractHashCode;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class HashCodeMurmur
extends AbstractHashCode {
    @Override
    public int doHash(String text) {
        ByteBuffer buf = ByteBuffer.wrap(text.getBytes());
        int seed = 305441741;
        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);
        long m = -4132994306676758123L;
        int r = 47;
        long h = (long)seed ^ (long)buf.remaining() * m;
        while (buf.remaining() >= 8) {
            long k = buf.getLong();
            k *= m;
            k ^= k >>> r;
            h ^= (k *= m);
            h *= m;
        }
        if (buf.remaining() > 0) {
            ByteBuffer finish = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
            finish.put(buf).rewind();
            h ^= finish.getLong();
            h *= m;
        }
        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;
        buf.order(byteOrder);
        return (int)(h & 0xFFFFFFFFL);
    }
}

