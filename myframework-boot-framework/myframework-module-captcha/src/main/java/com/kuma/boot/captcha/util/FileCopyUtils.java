/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.captcha.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.OpenOption;

public class FileCopyUtils {
    public static final int BUFFER_SIZE = 4096;

    public static int copy(File in, File out) throws IOException {
        return FileCopyUtils.copy(Files.newInputStream(in.toPath(), new OpenOption[0]), Files.newOutputStream(out.toPath(), new OpenOption[0]));
    }

    public static void copy(byte[] in, File out) throws IOException {
        FileCopyUtils.copy(new ByteArrayInputStream(in), Files.newOutputStream(out.toPath(), new OpenOption[0]));
    }

    public static byte[] copyToByteArray(File in) throws IOException {
        return FileCopyUtils.copyToByteArray(Files.newInputStream(in.toPath(), new OpenOption[0]));
    }

    public static int copy(InputStream in, OutputStream out) throws IOException {
        int var2;
        try (InputStream inputStream = in;
             OutputStream outputStream = out;){
            var2 = StreamUtils.copy(in, out);
        }
        return var2;
    }

    public static void copy(byte[] in, OutputStream out) throws IOException {
        try (OutputStream outputStream = out;){
            out.write(in);
        }
    }

    public static byte[] copyToByteArray(InputStream in) throws IOException {
        if (in == null) {
            return new byte[0];
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        FileCopyUtils.copy(in, (OutputStream)out);
        return out.toByteArray();
    }

    public static int copy(Reader in, Writer out) throws IOException {
        try (Reader reader = in;){
            int n;
            block13: {
                Writer writer = out;
                try {
                    int bytesRead;
                    int byteCount = 0;
                    char[] buffer = new char[4096];
                    boolean var4 = true;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                        byteCount += bytesRead;
                    }
                    out.flush();
                    n = byteCount;
                    if (writer == null) break block13;
                }
                catch (Throwable throwable) {
                    if (writer != null) {
                        try {
                            writer.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                writer.close();
            }
            return n;
        }
    }

    public static void copy(String in, Writer out) throws IOException {
        try (Writer writer = out;){
            out.write(in);
        }
    }

    public static String copyToString(Reader in) throws IOException {
        if (in == null) {
            return "";
        }
        StringWriter out = new StringWriter();
        FileCopyUtils.copy(in, (Writer)out);
        return out.toString();
    }
}

