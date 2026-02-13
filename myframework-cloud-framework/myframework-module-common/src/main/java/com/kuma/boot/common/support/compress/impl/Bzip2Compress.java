/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
 *  org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
 */
package com.kuma.boot.common.support.compress.impl;

import com.kuma.boot.common.support.compress.Compress;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

public class Bzip2Compress
implements Compress {
    @Override
    public byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BZip2CompressorOutputStream bcos = new BZip2CompressorOutputStream((OutputStream)out);
        bcos.write(data);
        bcos.close();
        return out.toByteArray();
    }

    @Override
    public byte[] uncompress(byte[] data) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        try {
            int n;
            BZip2CompressorInputStream ungzip = new BZip2CompressorInputStream((InputStream)in);
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

