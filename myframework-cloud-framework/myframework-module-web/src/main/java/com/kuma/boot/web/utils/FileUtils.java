/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  jakarta.servlet.ServletOutputStream
 *  jakarta.servlet.http.HttpServletResponse
 *  org.apache.commons.io.FilenameUtils
 *  org.apache.commons.lang3.ArrayUtils
 *  org.springframework.core.io.FileSystemResource
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.MediaType
 *  org.springframework.http.ResponseEntity
 *  org.springframework.http.ResponseEntity$BodyBuilder
 */
package com.kuma.boot.web.utils;

import com.google.common.base.Preconditions;
import com.kuma.boot.common.utils.lang.StringUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class FileUtils {
    public static final String[] VALID_FILE_TYPE = new String[]{"xlsx", "zip"};
    public static final String IMAGES_STR = "png,jpg,jpeg,gif,tif,bmp";
    public static final String VIDEO_STR = "avi,wmv,mpeg,mp4,mov,flv,rm,rmvb,3gp";

    public static int getFileType(String fileName) {
        String fileType = FilenameUtils.getExtension((String)fileName);
        assert (fileType != null);
        int type = 3;
        if (IMAGES_STR.contains(fileType)) {
            type = 1;
        } else if (VIDEO_STR.contains(fileType)) {
            type = 2;
        }
        return type;
    }

    private static String getFileType(File file) throws Exception {
        Preconditions.checkNotNull((Object)file);
        if (file.isDirectory()) {
            throw new Exception("file\u4e0d\u662f\u6587\u4ef6");
        }
        String fileName = file.getName();
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private static Boolean fileTypeIsValid(String fileType) {
        Preconditions.checkNotNull((Object)fileType);
        fileType = StringUtils.toLowerCase((CharSequence)fileType);
        return ArrayUtils.contains((Object[])VALID_FILE_TYPE, (Object)fileType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void download(String filePath, String fileName, Boolean delete, HttpServletResponse response) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new Exception("\u6587\u4ef6\u672a\u627e\u5230");
        }
        String fileType = FileUtils.getFileType(file);
        if (!FileUtils.fileTypeIsValid(fileType).booleanValue()) {
            throw new Exception("\u6682\u4e0d\u652f\u6301\u8be5\u7c7b\u578b\u6587\u4ef6\u4e0b\u8f7d");
        }
        response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        response.setContentType("multipart/form-data");
        response.setCharacterEncoding("utf-8");
        try (FileInputStream inputStream = new FileInputStream(file);
             ServletOutputStream os = response.getOutputStream();){
            int length;
            byte[] b = new byte[2048];
            while ((length = ((InputStream)inputStream).read(b)) > 0) {
                os.write(b, 0, length);
            }
        }
        finally {
            if (delete.booleanValue()) {
                FileUtils.delete(filePath);
            }
        }
    }

    public static void delete(String filePath) {
        File[] files;
        File file = new File(filePath);
        if (file.isDirectory() && (files = file.listFiles()) != null) {
            Arrays.stream(files).forEach(f -> FileUtils.delete(f.getPath()));
        }
        file.delete();
    }

    public static ResponseEntity<FileSystemResource> export(File file) {
        if (file == null) {
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ((ResponseEntity.BodyBuilder)ResponseEntity.ok().headers(headers)).contentLength(file.length()).contentType(MediaType.parseMediaType((String)"application/octet-stream")).body((Object)new FileSystemResource(file));
    }
}

