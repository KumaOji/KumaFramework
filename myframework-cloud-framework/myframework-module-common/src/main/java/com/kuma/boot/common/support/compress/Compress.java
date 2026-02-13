/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.compress;

import com.kuma.boot.common.extension.SPI;
import java.io.IOException;

@SPI(value="Compress")
public interface Compress {
    public byte[] compress(byte[] var1) throws IOException;

    public byte[] uncompress(byte[] var1) throws IOException;
}

