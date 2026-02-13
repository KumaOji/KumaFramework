/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.web.multipart.MultipartFile
 */
package com.kuma.boot.common.model;

import com.kuma.boot.common.utils.log.LogUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import org.springframework.web.multipart.MultipartFile;

public class Base64DecodeMultipartFile
implements MultipartFile {
    private final byte[] imgContent;
    private final String header;

    public Base64DecodeMultipartFile(byte[] imgContent, String header) {
        this.imgContent = imgContent;
        this.header = header.split(";")[0];
    }

    public String getName() {
        return (double)System.currentTimeMillis() + Math.random() + "." + this.header.split("/")[1];
    }

    public String getOriginalFilename() {
        return System.currentTimeMillis() + (long)((int)Math.random() * 10000) + "." + this.header.split("/")[1];
    }

    public String getContentType() {
        return this.header.split(":")[1];
    }

    public boolean isEmpty() {
        return this.imgContent == null || this.imgContent.length == 0;
    }

    public long getSize() {
        return this.imgContent.length;
    }

    public byte[] getBytes() throws IOException {
        return this.imgContent;
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.imgContent);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void transferTo(File dest) throws IOException, IllegalStateException {
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(dest);
            ((OutputStream)stream).write(this.imgContent);
        }
        catch (IOException e) {
            LogUtils.error("transferTo\u9519\u8bef", e);
        }
        finally {
            assert (stream != null);
            ((OutputStream)stream).close();
        }
    }

    public static MultipartFile base64Convert(String base64) {
        String[] baseStrs = base64.split(",");
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] b = decoder.decode(baseStrs[1]);
        for (int i = 0; i < b.length; ++i) {
            if (b[i] >= 0) continue;
            int n = i;
            b[n] = (byte)(b[n] + 256);
        }
        return new Base64DecodeMultipartFile(b, baseStrs[0]);
    }

    public static InputStream base64ToInputStream(String base64) {
        ByteArrayInputStream stream = null;
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            stream = new ByteArrayInputStream(bytes);
        }
        catch (Exception e) {
            LogUtils.error("base64ToInputStream\u9519\u8bef", e);
        }
        return stream;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static String inputStreamToStream(InputStream in) {
        byte[] data = null;
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = in.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        data = swapStream.toByteArray();
        if (in == null) return Base64.getEncoder().encodeToString(data);
        try {
            in.close();
            return Base64.getEncoder().encodeToString(data);
        }
        catch (IOException e) {
            LogUtils.error("inputStreamToStream\u9519\u8bef", e);
        }
        return Base64.getEncoder().encodeToString(data);
        catch (IOException e) {
            try {
                LogUtils.error("\u8f6c\u7801\u9519\u8bef", e);
                if (in == null) return Base64.getEncoder().encodeToString(data);
            }
            catch (Throwable throwable) {
                if (in == null) throw throwable;
                try {
                    in.close();
                    throw throwable;
                }
                catch (IOException e2) {
                    LogUtils.error("inputStreamToStream\u9519\u8bef", e2);
                }
                throw throwable;
            }
            try {
                in.close();
                return Base64.getEncoder().encodeToString(data);
            }
            catch (IOException e3) {
                LogUtils.error("inputStreamToStream\u9519\u8bef", e3);
            }
            return Base64.getEncoder().encodeToString(data);
        }
    }
}

