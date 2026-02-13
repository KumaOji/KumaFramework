/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.apache.commons.io.IOUtils
 */
package com.kuma.boot.common.utils.secure;

import com.kuma.boot.common.utils.log.LogUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

public class CryptoUtils {
    private static final String DEFAULT_CHARSET = "UTF-8";

    private CryptoUtils() {
    }

    public static String encodeMD5(byte[] bytes) {
        return DigestUtils.md5Hex((byte[])bytes);
    }

    public static String encodeMD5(String str) {
        return CryptoUtils.encodeMD5(str, DEFAULT_CHARSET);
    }

    public static String encodeMD5(String str, String charset) {
        if (str == null) {
            return null;
        }
        try {
            byte[] bytes = str.getBytes(charset);
            return CryptoUtils.encodeMD5(bytes);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeSHA(byte[] bytes) {
        return DigestUtils.sha512Hex((byte[])bytes);
    }

    public static String encodeSHA(String str, String charset) {
        if (str == null) {
            return null;
        }
        try {
            byte[] bytes = str.getBytes(charset);
            return CryptoUtils.encodeSHA(bytes);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeSHA(String str) {
        return CryptoUtils.encodeSHA(str, DEFAULT_CHARSET);
    }

    public static String encodeBASE64(byte[] bytes) {
        return new String(Base64.encodeBase64String((byte[])bytes));
    }

    public static String encodeBASE64(String str, String charset) {
        if (str == null) {
            return null;
        }
        try {
            byte[] bytes = str.getBytes(charset);
            return CryptoUtils.encodeBASE64(bytes);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeBASE64(String str) {
        return CryptoUtils.encodeBASE64(str, DEFAULT_CHARSET);
    }

    public static String decodeBASE64(String str) {
        return CryptoUtils.decodeBASE64(str, DEFAULT_CHARSET);
    }

    public static String decodeBASE64(String str, String charset) {
        try {
            byte[] bytes = str.getBytes(charset);
            return new String(Base64.decodeBase64((byte[])bytes));
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String crc32(byte[] bytes) {
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return Long.toHexString(crc32.getValue());
    }

    public static String crc32(String str, String charset) {
        try {
            byte[] bytes = str.getBytes(charset);
            return CryptoUtils.crc32(bytes);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String crc32(String str) {
        return CryptoUtils.crc32(str, DEFAULT_CHARSET);
    }

    public static String crc32(InputStream input) {
        CRC32 crc32 = new CRC32();
        CheckedInputStream checkInputStream = null;
        int test = 0;
        try {
            checkInputStream = new CheckedInputStream(input, crc32);
            while ((test = checkInputStream.read()) != -1) {
            }
            return Long.toHexString(crc32.getValue());
        }
        catch (IOException e) {
            LogUtils.error(e);
            throw new RuntimeException(e);
        }
    }

    public static String crc32(File file) {
        String string;
        BufferedInputStream input = null;
        try {
            input = new BufferedInputStream(new FileInputStream(file));
            string = CryptoUtils.crc32(input);
        }
        catch (FileNotFoundException e) {
            try {
                LogUtils.error(e);
                throw new RuntimeException(e);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(input);
                throw throwable;
            }
        }
        IOUtils.closeQuietly((InputStream)input);
        return string;
    }

    public static String crc32(URL url) {
        InputStream input = null;
        try {
            input = url.openStream();
            String string = CryptoUtils.crc32(input);
            return string;
        }
        catch (IOException e) {
            LogUtils.error(e);
            throw new RuntimeException(e);
        }
        finally {
            IOUtils.closeQuietly((InputStream)input);
        }
    }
}

