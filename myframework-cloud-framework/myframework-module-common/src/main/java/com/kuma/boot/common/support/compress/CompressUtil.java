/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.compress.archivers.ArchiveEntry
 *  org.apache.commons.compress.archivers.ArchiveOutputStream
 *  org.apache.commons.compress.archivers.zip.ZipArchiveEntry
 *  org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
 *  org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
 *  org.apache.commons.io.IOUtils
 */
package com.kuma.boot.common.support.compress;

import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;

public class CompressUtil {
    private CompressUtil() {
    }

    public static void zip(String srcDir, String targetFile) {
        try (FileOutputStream outputStream = new FileOutputStream(targetFile);){
            CompressUtil.zip(srcDir, outputStream);
        }
        catch (Exception exp) {
            LogUtils.error(exp);
            throw new BaseException("\u538b\u7f29\u6587\u4ef6\u51fa\u9519", (Throwable)exp);
        }
    }

    public static void zip(String srcDir, OutputStream outputStream) {
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
             ZipArchiveOutputStream out = new ZipArchiveOutputStream((OutputStream)bufferedOutputStream);){
            final Path start = Paths.get(srcDir, new String[0]);
            Files.walkFileTree(start, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>((ArchiveOutputStream)out){
                final /* synthetic */ ArchiveOutputStream val$out;
                {
                    this.val$out = archiveOutputStream;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    ZipArchiveEntry entry = new ZipArchiveEntry(dir.toFile(), start.relativize(dir).toString());
                    this.val$out.putArchiveEntry((ArchiveEntry)entry);
                    this.val$out.closeArchiveEntry();
                    return super.preVisitDirectory(dir, attrs);
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try (FileInputStream input = new FileInputStream(file.toFile());){
                        ZipArchiveEntry entry = new ZipArchiveEntry(file.toFile(), start.relativize(file).toString());
                        this.val$out.putArchiveEntry((ArchiveEntry)entry);
                        IOUtils.copy((InputStream)input, (OutputStream)this.val$out);
                        this.val$out.closeArchiveEntry();
                    }
                    return super.visitFile(file, attrs);
                }
            });
        }
        catch (Exception exp) {
            throw new BaseException("\u538b\u7f29\u6587\u4ef6\u51fa\u9519", (Throwable)exp);
        }
    }

    public static void unzip(String zipFileName, String destDir) {
        try (FileInputStream inputStream = new FileInputStream(zipFileName);){
            CompressUtil.unzip(inputStream, destDir);
        }
        catch (Exception exp) {
            throw new BaseException("\u89e3\u538b\u6587\u4ef6\u51fa\u9519", (Throwable)exp);
        }
    }

    public static void unzip(InputStream inputStream, String destDir) {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
             ZipArchiveInputStream in = new ZipArchiveInputStream((InputStream)bufferedInputStream);){
            ArchiveEntry entry = null;
            while (Objects.nonNull(entry = in.getNextEntry())) {
                if (in.canReadEntryData(entry)) {
                    File file = Paths.get(destDir, entry.getName()).toFile();
                    if (entry.isDirectory()) {
                        if (file.exists()) continue;
                        file.mkdirs();
                        continue;
                    }
                    FileOutputStream out = new FileOutputStream(file);
                    try {
                        IOUtils.copy((InputStream)in, (OutputStream)out);
                        continue;
                    }
                    finally {
                        ((OutputStream)out).close();
                        continue;
                    }
                }
                LogUtils.info(entry.getName(), new Object[0]);
            }
        }
        catch (Exception exp) {
            LogUtils.error(exp);
            throw new BaseException("\u89e3\u538b\u6587\u4ef6\u51fa\u9519", (Throwable)exp);
        }
    }
}

