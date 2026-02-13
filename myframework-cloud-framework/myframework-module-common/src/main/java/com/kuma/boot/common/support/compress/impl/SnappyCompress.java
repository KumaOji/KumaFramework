/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.xerial.snappy.Snappy
 */
package com.kuma.boot.common.support.compress.impl;

import com.kuma.boot.common.support.compress.Compress;
import java.io.IOException;
import org.xerial.snappy.Snappy;

public class SnappyCompress
implements Compress {
    @Override
    public byte[] compress(byte[] data) throws IOException {
        return Snappy.compress((byte[])data);
    }

    @Override
    public byte[] uncompress(byte[] data) throws IOException {
        return Snappy.uncompress((byte[])data);
    }
}

