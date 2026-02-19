/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.io.FileUtil
 *  cn.hutool.core.util.ZipUtil
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.apache.commons.io.FileUtils
 */
package com.kuma.boot.office.convert.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;

public class MyFileUtil {
    public static File zip(String srcPath, String zipPath, boolean isWithSrcDir, boolean isDeleteSrcZip) {
        LogUtils.debug((String)"\u3010\u538b\u7f29\u6587\u4ef6\u3011 \u6e90\u76ee\u5f55\u8def\u5f84: \u3010{}\u3011 \u6253\u5305\u540e\u7684\u8def\u5f84+\u6587\u4ef6\u540e\u7f00\u540d: \u3010{}\u3011", (Object[])new Object[]{srcPath, zipPath});
        File zipFile = ZipUtil.zip((String)srcPath, (String)zipPath, (boolean)isWithSrcDir);
        if (isDeleteSrcZip) {
            boolean result = MyFileUtil.deleteFileOrFolder(srcPath);
            LogUtils.info((String)"\u5220\u9664\u6210\u529f", (Object[])new Object[]{result});
        }
        return zipFile;
    }

    public static boolean deleteFileOrFolder(String fullFileOrDirPath) {
        FileUtil.del((String)fullFileOrDirPath);
        return true;
    }

    public static File touch(String fullFilePath) {
        return FileUtil.touch((String)fullFilePath);
    }

    public static File unzip(InputStream inputStream, String zipFilePath, String outFileDir, boolean isDeleteZip) throws IOException {
        LogUtils.debug((String)"\u3010\u89e3\u538b\u6587\u4ef6\u3011 zip\u6587\u4ef6\u8def\u5f84: \u3010{}\u3011 \u89e3\u538b\u540e\u7684\u76ee\u5f55\u8def\u5f84: \u3010{}\u3011", (Object[])new Object[]{zipFilePath, outFileDir});
        File zipFile = FileUtil.newFile((String)zipFilePath);
        FileUtils.copyInputStreamToFile((InputStream)inputStream, (File)zipFile);
        File outFile = ZipUtil.unzip((String)zipFilePath, (String)outFileDir, (Charset)Charset.forName("GBK"));
        if (isDeleteZip) {
            MyFileUtil.deleteFileOrFolder(zipFilePath);
        }
        return outFile;
    }

    public static String readFileContent(File file) {
        return FileUtil.readUtf8String((File)file);
    }

    public static String readFileContent(String filePath) {
        return FileUtil.readUtf8String((String)filePath);
    }

    public static byte[] readBytes(String filePath) {
        return FileUtil.readBytes((String)filePath);
    }

    public static File writeFileContent(String fileContent, String filePath) {
        return FileUtil.writeUtf8String((String)fileContent, (String)filePath);
    }

    public static File writeFileContent(byte[] data, String filePath) {
        return FileUtil.writeBytes((byte[])data, (String)filePath);
    }
}

