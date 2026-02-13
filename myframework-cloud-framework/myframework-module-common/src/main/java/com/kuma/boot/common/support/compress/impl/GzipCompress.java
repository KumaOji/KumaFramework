/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.compress.impl;

import com.kuma.boot.common.support.compress.Compress;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipCompress
implements Compress {
    @Override
    public byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            gzip.write(data);
            gzip.close();
        }
        catch (IOException e) {
            LogUtils.error(e);
        }
        return out.toByteArray();
    }

    @Override
    public byte[] uncompress(byte[] data) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        try {
            int n;
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[2048];
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        }
        catch (IOException e) {
            LogUtils.error(e);
        }
        return out.toByteArray();
    }
}

