/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.io;

import com.kuma.boot.common.exception.BootException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class RandomAccessFileUtil {
    private RandomAccessFileUtil() {
    }

    public static String getFileContent(String filePath, int startIndex, int endIndex) {
        return RandomAccessFileUtil.getFileContent(filePath, startIndex, endIndex, StandardCharsets.UTF_8);
    }

    public static String getFileContent(String filePath, int startIndex, int endIndex, Charset charset) {
        String string;
        int size = endIndex - startIndex;
        RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "r");
        try {
            MappedByteBuffer inputBuffer = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, startIndex, size);
            byte[] bs = new byte[size];
            for (int offset = 0; offset < inputBuffer.capacity(); ++offset) {
                bs[offset] = inputBuffer.get(offset);
            }
            string = new String(bs, charset);
        }
        catch (Throwable throwable) {
            try {
                try {
                    randomAccessFile.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                throw new BootException(e);
            }
        }
        randomAccessFile.close();
        return string;
    }
}

