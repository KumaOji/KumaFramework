/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 */
package com.kuma.boot.common.utils.io;

import com.kuma.boot.common.utils.io.ByteBufferInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.apache.commons.io.IOUtils;

public final class ByteBufferUtils {
    private ByteBufferUtils() {
    }

    public static ByteBuffer newByteBuffer(byte[] bytes) {
        int len = bytes.length;
        ByteBuffer byteBuffer = ByteBuffer.allocate(len);
        byteBuffer.put(bytes);
        byteBuffer.flip();
        return byteBuffer;
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public static byte[] toByteArray(ByteBuffer byteBuffer) {
        if (byteBuffer.isDirect()) {
            byteBuffer.rewind();
            try (ByteBufferInputStream input = new ByteBufferInputStream(byteBuffer);){
                byte[] byArray;
                try (ByteArrayOutputStream output = new ByteArrayOutputStream();){
                    IOUtils.copy((InputStream)input, (OutputStream)output);
                    byArray = output.toByteArray();
                }
                return byArray;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return byteBuffer.array();
    }
}

