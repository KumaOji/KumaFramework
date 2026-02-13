/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 *  org.springframework.util.StreamUtils
 */
package com.kuma.boot.common.utils.io;

import com.kuma.boot.common.utils.exception.ExceptionUtils;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.jspecify.annotations.Nullable;
import org.springframework.util.StreamUtils;

public class IoUtils
extends StreamUtils {
    public static void closeQuietly(@Nullable Closeable closeable) {
        if (closeable == null) {
            return;
        }
        if (closeable instanceof Flushable) {
            try {
                ((Flushable)((Object)closeable)).flush();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        try {
            closeable.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static String readToString(InputStream input) {
        return IoUtils.readToString(input, StandardCharsets.UTF_8);
    }

    public static String readToString(@Nullable InputStream input, Charset charset) {
        try {
            String string = IoUtils.copyToString((InputStream)input, (Charset)charset);
            return string;
        }
        catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
        finally {
            IoUtils.closeQuietly(input);
        }
    }

    public static byte[] readToByteArray(@Nullable InputStream input) {
        try {
            byte[] byArray = IoUtils.copyToByteArray((InputStream)input);
            return byArray;
        }
        catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
        finally {
            IoUtils.closeQuietly(input);
        }
    }

    public static void write(@Nullable String data, OutputStream output, Charset encoding) throws IOException {
        if (data != null) {
            output.write(data.getBytes(encoding));
        }
    }
}

