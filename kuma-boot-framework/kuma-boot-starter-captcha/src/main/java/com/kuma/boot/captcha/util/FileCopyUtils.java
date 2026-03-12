//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

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

public class FileCopyUtils {
    public static final int BUFFER_SIZE = 4096;

    public static int copy(File in, File out) throws IOException {
        return copy(Files.newInputStream(in.toPath()), Files.newOutputStream(out.toPath()));
    }

    public static void copy(byte[] in, File out) throws IOException {
        copy((InputStream)(new ByteArrayInputStream(in)), (OutputStream)Files.newOutputStream(out.toPath()));
    }

    public static byte[] copyToByteArray(File in) throws IOException {
        return copyToByteArray(Files.newInputStream(in.toPath()));
    }

    public static int copy(InputStream in, OutputStream out) throws IOException {
        InputStream var3 = in;

        int var2;
        try {
            OutputStream var4 = out;

            try {
                var2 = StreamUtils.copy(in, out);
            } catch (Throwable var9) {
                if (out != null) {
                    try {
                        var4.close();
                    } catch (Throwable var8) {
                        var9.addSuppressed(var8);
                    }
                }

                throw var9;
            }

            if (out != null) {
                out.close();
            }
        } catch (Throwable var10) {
            if (in != null) {
                try {
                    var3.close();
                } catch (Throwable var7) {
                    var10.addSuppressed(var7);
                }
            }

            throw var10;
        }

        if (in != null) {
            in.close();
        }

        return var2;
    }

    public static void copy(byte[] in, OutputStream out) throws IOException {
        OutputStream var2 = out;

        try {
            out.write(in);
        } catch (Throwable var6) {
            if (out != null) {
                try {
                    var2.close();
                } catch (Throwable var5) {
                    var6.addSuppressed(var5);
                }
            }

            throw var6;
        }

        if (out != null) {
            out.close();
        }

    }

    public static byte[] copyToByteArray(InputStream in) throws IOException {
        if (in == null) {
            return new byte[0];
        } else {
            ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
            copy((InputStream)in, (OutputStream)out);
            return out.toByteArray();
        }
    }

    public static int copy(Reader in, Writer out) throws IOException {
        Reader var2 = in;

        int var12;
        try {
            Writer var3 = out;

            try {
                int byteCount = 0;
                char[] buffer = new char[4096];

                int bytesRead;
                for(boolean var4 = true; (bytesRead = in.read(buffer)) != -1; byteCount += bytesRead) {
                    out.write(buffer, 0, bytesRead);
                }

                out.flush();
                var12 = byteCount;
            } catch (Throwable var10) {
                if (out != null) {
                    try {
                        var3.close();
                    } catch (Throwable var9) {
                        var10.addSuppressed(var9);
                    }
                }

                throw var10;
            }

            if (out != null) {
                out.close();
            }
        } catch (Throwable var11) {
            if (in != null) {
                try {
                    var2.close();
                } catch (Throwable var8) {
                    var11.addSuppressed(var8);
                }
            }

            throw var11;
        }

        if (in != null) {
            in.close();
        }

        return var12;
    }

    public static void copy(String in, Writer out) throws IOException {
        Writer var2 = out;

        try {
            out.write(in);
        } catch (Throwable var6) {
            if (out != null) {
                try {
                    var2.close();
                } catch (Throwable var5) {
                    var6.addSuppressed(var5);
                }
            }

            throw var6;
        }

        if (out != null) {
            out.close();
        }

    }

    public static String copyToString(Reader in) throws IOException {
        if (in == null) {
            return "";
        } else {
            StringWriter out = new StringWriter();
            copy((Reader)in, (Writer)out);
            return out.toString();
        }
    }
}
