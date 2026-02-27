/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

public class StreamUtils {
    public static final int BUFFER_SIZE = 4096;
    private static final byte[] EMPTY_CONTENT = new byte[0];

    public static byte[] copyToByteArray(InputStream in) throws IOException {
        if (in == null) {
            return new byte[0];
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        StreamUtils.copy(in, (OutputStream)out);
        return out.toByteArray();
    }

    public static String copyToString(InputStream in, Charset charset) throws IOException {
        int bytesRead;
        if (in == null) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(in, charset);
        char[] buffer = new char[4096];
        boolean var5 = true;
        while ((bytesRead = reader.read(buffer)) != -1) {
            out.append(buffer, 0, bytesRead);
        }
        return out.toString();
    }

    public static void copy(byte[] in, OutputStream out) throws IOException {
        out.write(in);
    }

    public static void copy(String in, Charset charset, OutputStream out) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out, charset);
        writer.write(in);
        ((Writer)writer).flush();
    }

    public static int copy(InputStream in, OutputStream out) throws IOException {
        int bytesRead;
        int byteCount = 0;
        byte[] buffer = new byte[4096];
        boolean var4 = true;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
            byteCount += bytesRead;
        }
        out.flush();
        return byteCount;
    }

    public static long copyRange(InputStream in, OutputStream out, long start, long end) throws IOException {
        int bytesRead;
        long skipped = in.skip(start);
        if (skipped < start) {
            throw new IOException("Skipped only " + skipped + " bytes out of " + start + " required");
        }
        long bytesToCopy = end - start + 1L;
        byte[] buffer = new byte[4096];
        while (bytesToCopy > 0L && (bytesRead = in.read(buffer)) != -1) {
            if ((long)bytesRead <= bytesToCopy) {
                out.write(buffer, 0, bytesRead);
                bytesToCopy -= (long)bytesRead;
                continue;
            }
            out.write(buffer, 0, (int)bytesToCopy);
            bytesToCopy = 0L;
        }
        return end - start + 1L - bytesToCopy;
    }

    public static int drain(InputStream in) throws IOException {
        int bytesRead;
        byte[] buffer = new byte[4096];
        int byteCount = 0;
        while ((bytesRead = in.read(buffer)) != -1) {
            byteCount += bytesRead;
        }
        return byteCount;
    }

    public static InputStream emptyInput() {
        return new ByteArrayInputStream(EMPTY_CONTENT);
    }

    public static InputStream nonClosing(InputStream in) {
        return new NonClosingInputStream(in);
    }

    public static OutputStream nonClosing(OutputStream out) {
        return new NonClosingOutputStream(out);
    }

    private static class NonClosingInputStream
    extends FilterInputStream {
        public NonClosingInputStream(InputStream in) {
            super(in);
        }

        @Override
        public void close() throws IOException {
        }
    }

    private static class NonClosingOutputStream
    extends FilterOutputStream {
        public NonClosingOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void write(byte[] b, int off, int let) throws IOException {
            this.out.write(b, off, let);
        }

        @Override
        public void close() throws IOException {
        }
    }
}

