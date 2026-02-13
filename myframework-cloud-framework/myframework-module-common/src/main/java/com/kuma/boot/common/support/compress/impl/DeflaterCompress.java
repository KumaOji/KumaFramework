/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.compress.impl;

import com.kuma.boot.common.support.compress.Compress;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class DeflaterCompress
implements Compress {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Deflater compressor = new Deflater(1);
        try {
            compressor.setInput(data);
            compressor.finish();
            byte[] buf = new byte[2048];
            while (!compressor.finished()) {
                int count = compressor.deflate(buf);
                bos.write(buf, 0, count);
            }
        }
        finally {
            compressor.end();
        }
        return bos.toByteArray();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] uncompress(byte[] data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Inflater decompressor = new Inflater();
        try {
            decompressor.setInput(data);
            byte[] buf = new byte[2048];
            while (!decompressor.finished()) {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            }
        }
        catch (DataFormatException e) {
            LogUtils.error(e);
        }
        finally {
            decompressor.end();
        }
        return bos.toByteArray();
    }
}

