/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.lang.Assert
 *  jakarta.annotation.Nullable
 *  org.springframework.http.codec.multipart.Part
 *  org.springframework.util.FileCopyUtils
 *  org.springframework.util.FileSystemUtils
 *  org.springframework.util.PatternMatchUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.web.multipart.MultipartFile
 *  org.springframework.web.server.ServerWebExchange
 *  reactor.core.publisher.Mono
 */
package com.kuma.boot.common.utils.io;

import cn.hutool.core.lang.Assert;
import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.support.handler.MapHandler;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.common.utils.collection.MapUtils;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.exception.ExceptionUtils;
import com.kuma.boot.common.utils.io.IoUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.annotation.Nullable;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public final class FileUtils
extends FileCopyUtils {
    private FileUtils() {
    }

    public static Mono<Part> getPartValues(String name, ServerWebExchange exchange) {
        return exchange.getMultipartData().mapNotNull(multiValueMap -> (Part)multiValueMap.getFirst((Object)name));
    }

    public static String getFileContent(String filePath) {
        return FileUtils.getFileContent(filePath, "UTF-8");
    }

    public static String getFileContent(String filePath, String charset) {
        File file = new File(filePath);
        if (file.exists()) {
            String string;
            FileInputStream inputStream = new FileInputStream(file);
            try {
                string = FileUtils.getFileContent(inputStream, charset);
            }
            catch (Throwable throwable) {
                try {
                    try {
                        ((InputStream)inputStream).close();
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
            ((InputStream)inputStream).close();
            return string;
        }
        throw new BootException(filePath + " is not exists!");
    }

    public static String getFileContent(InputStream inputStream) {
        return FileUtils.getFileContent(inputStream, "UTF-8");
    }

    public static String getFileContent(File file, String charset) {
        String string;
        FileInputStream inputStream = new FileInputStream(file);
        try {
            string = FileUtils.getFileContent(inputStream, charset);
        }
        catch (Throwable throwable) {
            try {
                try {
                    ((InputStream)inputStream).close();
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
        ((InputStream)inputStream).close();
        return string;
    }

    public static String getFileContent(File file) {
        String string;
        FileInputStream inputStream = new FileInputStream(file);
        try {
            string = FileUtils.getFileContent(inputStream);
        }
        catch (Throwable throwable) {
            try {
                try {
                    ((InputStream)inputStream).close();
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
        ((InputStream)inputStream).close();
        return string;
    }

    public static String getFileContent(InputStream inputStream, String charset) {
        Charset charsetVal = Charset.forName(charset);
        return FileUtils.getFileContent(inputStream, 0, Integer.MAX_VALUE, charsetVal);
    }

    public static String getFileContent(InputStream inputStream, int startIndex, int endIndex, Charset charset) {
        try {
            endIndex = Math.min(endIndex, inputStream.available());
            startIndex = Math.max(0, startIndex);
            inputStream.skip(startIndex);
            int count = endIndex - startIndex;
            byte[] bytes = new byte[count];
            for (int readCount = 0; readCount < count && readCount != -1; readCount += inputStream.read(bytes, readCount, count - readCount)) {
            }
            String string = new String(bytes, charset);
            return string;
        }
        catch (IOException e) {
            throw new BootException(e);
        }
        finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (IOException e) {
                LogUtils.error(e);
            }
        }
    }

    public static String getSuffix(String fileName) {
        return fileName.substring(fileName.lastIndexOf(46) + 1);
    }

    public static List<String> getFileContentEachLine(String filePath, int initLine) {
        File file = new File(filePath);
        return FileUtils.getFileContentEachLine(file, initLine);
    }

    public static List<String> getFileContentEachLine(String filePath) {
        File file = new File(filePath);
        return FileUtils.getFileContentEachLine(file, 0);
    }

    public static List<String> getFileContentEachLineTrim(String filePath, int initLine) {
        List<String> stringList = FileUtils.getFileContentEachLine(filePath, initLine);
        LinkedList<String> resultList = new LinkedList<String>();
        for (String string : stringList) {
            resultList.add(string.trim());
        }
        return resultList;
    }

    public static List<String> getFileContentEachLine(File file) {
        return FileUtils.getFileContentEachLine(file, 0);
    }

    public static List<String> getFileContentEachLine(File file, int initLine) {
        LinkedList<String> contentList = new LinkedList<String>();
        if (!file.exists()) {
            return contentList;
        }
        String charset = "UTF-8";
        try (FileInputStream fileInputStream = new FileInputStream(file);
             InputStreamReader inputStreamReader = new InputStreamReader((InputStream)fileInputStream, charset);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);){
            String dataEachLine;
            int lineNo;
            for (lineNo = 0; lineNo < initLine; ++lineNo) {
                String string = bufferedReader.readLine();
            }
            while ((dataEachLine = bufferedReader.readLine()) != null) {
                ++lineNo;
                if (Objects.equals("", dataEachLine)) continue;
                contentList.add(dataEachLine);
            }
        }
        catch (IOException e) {
            throw new BootException(e);
        }
        return contentList;
    }

    @Deprecated
    public static List<String> getFileContentEachLine(File file, int initLine, int endLine, String charset) {
        LinkedList<String> contentList = new LinkedList<String>();
        if (!file.exists()) {
            return contentList;
        }
        try (FileInputStream fileInputStream = new FileInputStream(file);
             InputStreamReader inputStreamReader = new InputStreamReader((InputStream)fileInputStream, charset);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);){
            String dataEachLine;
            int lineNo;
            for (lineNo = 0; lineNo < initLine; ++lineNo) {
                String string = bufferedReader.readLine();
            }
            while ((dataEachLine = bufferedReader.readLine()) != null && lineNo < endLine) {
                ++lineNo;
                contentList.add(dataEachLine);
            }
        }
        catch (IOException e) {
            throw new BootException(e);
        }
        return contentList;
    }

    public static List<String> readAllLines(File file, String charset, int initLine, int endLine, boolean ignoreEmpty) {
        List<String> list;
        ArgUtils.notNull(file, "file");
        ArgUtils.notEmpty(charset, "charset");
        if (!file.exists()) {
            throw new BootException("File not exists!");
        }
        FileInputStream inputStream = new FileInputStream(file);
        try {
            list = FileUtils.readAllLines(inputStream, charset, initLine, endLine, ignoreEmpty);
        }
        catch (Throwable throwable) {
            try {
                try {
                    inputStream.close();
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
        inputStream.close();
        return list;
    }

    public static List<String> readAllLines(InputStream inputStream, String charset, int initLine, int endLine, boolean ignoreEmpty) {
        ArgUtils.notNull(inputStream, "inputStream");
        ArgUtils.notEmpty(charset, "charset");
        LinkedList<String> contentList = new LinkedList<String>();
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charset);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);){
            String dataEachLine;
            int lineNo;
            for (lineNo = 0; lineNo < initLine; ++lineNo) {
                String string = bufferedReader.readLine();
            }
            while ((dataEachLine = bufferedReader.readLine()) != null && lineNo < endLine) {
                ++lineNo;
                if (ignoreEmpty && StringUtils.isEmpty(dataEachLine)) continue;
                contentList.add(dataEachLine);
            }
        }
        catch (IOException e) {
            throw new BootException(e);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                LogUtils.error(e);
            }
        }
        return contentList;
    }

    public static List<String> readAllLines(InputStream inputStream, String charset, int initLine, int endLine) {
        return FileUtils.readAllLines(inputStream, charset, initLine, endLine, true);
    }

    public static List<String> readAllLines(InputStream inputStream, String charset, int initLine) {
        return FileUtils.readAllLines(inputStream, charset, initLine, Integer.MAX_VALUE);
    }

    public static List<String> readAllLines(InputStream inputStream, String charset) {
        return FileUtils.readAllLines(inputStream, charset, 0);
    }

    public static List<String> readAllLines(InputStream inputStream) {
        return FileUtils.readAllLines(inputStream, "UTF-8");
    }

    public static List<String> readAllLines(String filePath, String charset, boolean ignoreEmpty) {
        File file = new File(filePath);
        return FileUtils.readAllLines(file, charset, 0, Integer.MAX_VALUE, ignoreEmpty);
    }

    public static List<String> readAllLines(File file, String charset, boolean ignoreEmpty) {
        return FileUtils.readAllLines(file, charset, 0, Integer.MAX_VALUE, ignoreEmpty);
    }

    public static List<String> readAllLines(File file, String charset) {
        return FileUtils.readAllLines(file, charset, false);
    }

    public static List<String> readAllLines(File file) {
        return FileUtils.readAllLines(file, "UTF-8");
    }

    public static List<String> readAllLines(String filePath, String charset) {
        return FileUtils.readAllLines(filePath, charset, false);
    }

    public static List<String> readAllLines(String filePath) {
        return FileUtils.readAllLines(filePath, "UTF-8");
    }

    public static void copyDir(String sourceDir, String targetDir) throws IOException {
        File file = new File(sourceDir);
        Object[] filePath = file.list();
        if (!new File(targetDir).exists()) {
            new File(targetDir).mkdir();
        }
        if (ArrayUtils.isNotEmpty(filePath)) {
            for (Object aFilePath : filePath) {
                if (new File(sourceDir + File.separator + (String)aFilePath).isDirectory()) {
                    FileUtils.copyDir(sourceDir + File.separator + (String)aFilePath, targetDir + File.separator + (String)aFilePath);
                }
                if (!new File(sourceDir + File.separator + (String)aFilePath).isFile()) continue;
                FileUtils.copyFile(sourceDir + File.separator + (String)aFilePath, targetDir + File.separator + (String)aFilePath);
            }
        }
    }

    public static void copyFile(String sourceFile, String targetPath) throws IOException {
        File oldFile = new File(sourceFile);
        File file = new File(targetPath);
        try (FileInputStream in = new FileInputStream(oldFile);
             FileOutputStream out = new FileOutputStream(file);){
            byte[] buffer = new byte[0x200000];
            while (in.read(buffer) != -1) {
                out.write(buffer);
            }
        }
    }

    public static void write(String filePath, CharSequence line, OpenOption ... openOptions) {
        FileUtils.write(filePath, Collections.singletonList(line), openOptions);
    }

    public static void write(String filePath, Iterable<? extends CharSequence> lines, OpenOption ... openOptions) {
        FileUtils.write(filePath, lines, "UTF-8", openOptions);
    }

    public static void write(String filePath, Iterable<? extends CharSequence> lines, String charset, OpenOption ... openOptions) {
        try {
            File parent;
            ArgUtils.notNull(lines, "charSequences");
            CharsetEncoder encoder = Charset.forName(charset).newEncoder();
            Path path = Paths.get(filePath, new String[0]);
            Path pathParent = path.getParent();
            if (pathParent != null && !(parent = pathParent.toFile()).exists()) {
                parent.mkdirs();
            }
            OutputStream out = path.getFileSystem().provider().newOutputStream(path, openOptions);
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, encoder));){
                for (CharSequence charSequence : lines) {
                    writer.append(charSequence);
                    writer.newLine();
                }
            }
        }
        catch (IOException e) {
            throw new BootException(e);
        }
    }

    public static boolean createFile(String filePath) {
        boolean mkdirResult;
        if (StringUtils.isEmpty(filePath)) {
            return false;
        }
        if (FileUtils.exists(filePath, new LinkOption[0])) {
            return true;
        }
        File file = new File(filePath);
        File dir = file.getParentFile();
        if (dir != null && FileUtils.notExists(dir) && !(mkdirResult = dir.mkdirs())) {
            return false;
        }
        try {
            return file.createNewFile();
        }
        catch (IOException e) {
            throw new BootException(e);
        }
    }

    public static boolean exists(String filePath, LinkOption ... options) {
        if (StringUtils.isEmpty(filePath)) {
            return false;
        }
        Path path = Paths.get(filePath, new String[0]);
        return Files.exists(path, options);
    }

    public static boolean notExists(String filePath, LinkOption ... options) {
        return !FileUtils.exists(filePath, options);
    }

    public static boolean notExists(File file) {
        ArgUtils.notNull(file, "file");
        return !file.exists();
    }

    public static boolean isEmpty(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return true;
        }
        File file = new File(filePath);
        return file.length() <= 0L;
    }

    public static boolean isNotEmpty(String filePath) {
        return !FileUtils.isEmpty(filePath);
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public static byte[] getFileBytes(File file) {
        ArgUtils.notNull(file, "file");
        try (FileInputStream fis = new FileInputStream(file);){
            byte[] byArray;
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);){
                int n;
                byte[] b = new byte[1024];
                while ((n = fis.read(b)) != -1) {
                    bos.write(b, 0, n);
                }
                byArray = bos.toByteArray();
            }
            return byArray;
        }
        catch (IOException e) {
            throw new BootException(e);
        }
    }

    public static byte[] getFileBytes(String filePath) {
        ArgUtils.notNull(filePath, "filePath");
        File file = new File(filePath);
        return FileUtils.getFileBytes(file);
    }

    public static void createFile(String filePath, byte[] bytes) {
        File file = FileUtils.createFileAssertSuccess(filePath);
        try (FileOutputStream fos = new FileOutputStream(file);
             BufferedOutputStream bos = new BufferedOutputStream(fos);){
            bos.write(bytes);
        }
        catch (Exception e) {
            throw new BootException(e);
        }
    }

    public static File createFileAssertSuccess(String filePath) {
        boolean mkdirResult;
        ArgUtils.notEmpty(filePath, "filePath");
        File file = new File(filePath);
        if (file.exists()) {
            return file;
        }
        File dir = file.getParentFile();
        if (FileUtils.notExists(dir) && !(mkdirResult = dir.mkdirs())) {
            throw new BootException("Parent file create fail " + filePath);
        }
        try {
            boolean createFile = file.createNewFile();
            if (!createFile) {
                throw new BootException("Create new file fail for path " + filePath);
            }
            return file;
        }
        catch (IOException e) {
            throw new BootException(e);
        }
    }

    public static void deleteFile(File file) {
        boolean result;
        ArgUtils.notNull(file, "file");
        if (file.exists() && !(result = file.delete())) {
            throw new BootException("Delete file fail for path " + file.getAbsolutePath());
        }
    }

    public static void deleteFile(String filePath) {
        ArgUtils.notEmpty(filePath, "filePath");
        File file = new File(filePath);
        FileUtils.deleteFile(file);
    }

    public static File createTempFile(String name, String suffix) {
        try {
            ArgUtils.notEmpty(name, "prefix");
            ArgUtils.notEmpty(suffix, "suffix");
            return File.createTempFile(name, suffix);
        }
        catch (IOException e) {
            throw new BootException(e);
        }
    }

    public static File createTempFile(String nameWithSuffix) {
        try {
            ArgUtils.notEmpty(nameWithSuffix, "fileName");
            String[] strings = nameWithSuffix.split("\\.");
            return File.createTempFile(strings[0], strings[1]);
        }
        catch (IOException e) {
            throw new BootException(e);
        }
    }

    public static boolean isImage(String string) {
        if (StringUtils.isEmpty(string)) {
            return false;
        }
        return string.endsWith(".png") || string.endsWith(".jpeg") || string.endsWith(".jpg") || string.endsWith(".gif");
    }

    public static <K, V> Map<K, V> readToMap(InputStream inputStream, String charset, MapHandler<K, V, String> mapHandler) {
        List<String> allLines = FileUtils.readAllLines(inputStream, charset);
        return MapUtils.toMap(allLines, mapHandler);
    }

    public static <K, V> Map<K, V> readToMap(String path, String charset, MapHandler<K, V, String> mapHandler) {
        List<String> allLines = FileUtils.readAllLines(path, charset);
        return MapUtils.toMap(allLines, mapHandler);
    }

    public static <K, V> Map<K, V> readToMap(String path, MapHandler<K, V, String> mapHandler) {
        return FileUtils.readToMap(path, "UTF-8", mapHandler);
    }

    public static Map<String, String> readToMap(String path) {
        return FileUtils.readToMap(path, " ");
    }

    public static Map<String, String> readToMap(String path, final String splitter) {
        return FileUtils.readToMap(path, new MapHandler<String, String, String>(){

            @Override
            public String getKey(String o) {
                return o.split(splitter)[0];
            }

            @Override
            public String getValue(String o) {
                return o.split(splitter)[1];
            }
        });
    }

    public static String getFileName(String path) {
        if (StringUtils.isEmptyTrim(path)) {
            return "";
        }
        File file = new File(path);
        String name = file.getName();
        return name.substring(0, name.lastIndexOf(46));
    }

    public static String getDirPath(String path) {
        Path path1 = Paths.get(path, new String[0]);
        return path1.getParent().toAbsolutePath().toString() + File.separator;
    }

    public static String trimWindowsSpecialChars(String name) {
        if (StringUtils.isEmpty(name)) {
            return name;
        }
        return name.replaceAll("[?/\\\\*<>|:\"]", "");
    }

    public static boolean rename(String sourcePath, String targetPath) {
        File sourceFile = new File(sourcePath);
        File targetFile = new File(targetPath);
        return sourceFile.renameTo(targetFile);
    }

    public static void merge(String result, String ... sources) {
        ArgUtils.notEmpty(result, "result");
        ArgUtils.notEmpty(sources, "sources");
        try (FileOutputStream os = new FileOutputStream(result);){
            for (String source : sources) {
                byte[] bytes = FileUtils.getFileBytes(source);
                ((OutputStream)os).write(bytes);
            }
        }
        catch (IOException e) {
            throw new BootException(e);
        }
    }

    public static void merge(String result, byte[] ... byteArrays) {
        ArgUtils.notEmpty(result, "result");
        ArgUtils.notEmpty((Object[])byteArrays, "byteArrays");
        try (FileOutputStream os = new FileOutputStream(result);){
            for (byte[] bytes : byteArrays) {
                ((OutputStream)os).write(bytes);
            }
        }
        catch (IOException e) {
            throw new BootException(e);
        }
    }

    public static void merge(String result, List<byte[]> byteArrayList) {
        ArgUtils.notEmpty(result, "result");
        ArgUtils.notEmpty(byteArrayList, "byteArrayList");
        try (FileOutputStream os = new FileOutputStream(result);){
            for (byte[] bytes : byteArrayList) {
                ((OutputStream)os).write(bytes);
            }
        }
        catch (IOException e) {
            throw new BootException(e);
        }
    }

    public static void write(String filePath, byte[] bytes) {
        ArgUtils.notEmpty(filePath, "filePath");
        try (FileOutputStream os = new FileOutputStream(filePath);){
            ((OutputStream)os).write(bytes);
        }
        catch (IOException e) {
            throw new BootException(e);
        }
    }

    public static String escapeWindowsSpecial(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return fileName;
        }
        return fileName.replaceAll("[\"<>/\\\\|:*?]", "");
    }

    public static boolean createDir(String dir) {
        if (StringUtils.isEmpty(dir)) {
            return false;
        }
        File file = new File(dir);
        if (file.isDirectory()) {
            return file.mkdirs();
        }
        return false;
    }

    public static void truncate(String filePath) {
        FileUtils.write(filePath, "", StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static void append(String filePath, String line) {
        FileUtils.write(filePath, line, StandardOpenOption.APPEND);
    }

    public static void append(String filePath, Collection<String> collection) {
        FileUtils.write(filePath, collection, StandardOpenOption.APPEND);
    }

    public static String fileToBase64(String filePath) {
        return filePath;
    }

    public static void base64ToFile(String base64Code, String targetPath) {
    }

    public static List<File> list(String path) {
        File file = new File(path);
        return FileUtils.list(file, (FileFilter)TrueFilter.TRUE);
    }

    public static List<File> list(String path, String fileNamePattern) {
        File file = new File(path);
        return FileUtils.list(file, (File pathname) -> {
            String fileName = pathname.getName();
            return PatternMatchUtils.simpleMatch((String)fileNamePattern, (String)fileName);
        });
    }

    public static List<File> list(String path, FileFilter filter) {
        File file = new File(path);
        return FileUtils.list(file, filter);
    }

    public static List<File> list(File file) {
        ArrayList<File> fileList = new ArrayList<File>();
        return FileUtils.list(file, fileList, TrueFilter.TRUE);
    }

    public static List<File> list(File file, String fileNamePattern) {
        ArrayList<File> fileList = new ArrayList<File>();
        return FileUtils.list(file, fileList, (File pathname) -> {
            String fileName = pathname.getName();
            return PatternMatchUtils.simpleMatch((String)fileNamePattern, (String)fileName);
        });
    }

    public static List<File> list(File file, String ... fileNamePatterns) {
        ArrayList<File> fileList = new ArrayList<File>();
        return FileUtils.list(file, fileList, (File pathname) -> {
            String fileName = pathname.getName();
            return PatternMatchUtils.simpleMatch((String[])fileNamePatterns, (String)fileName);
        });
    }

    public static List<File> list(File file, FileFilter filter) {
        ArrayList<File> fileList = new ArrayList<File>();
        return FileUtils.list(file, fileList, filter);
    }

    private static List<File> list(File file, List<File> fileList, FileFilter filter) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    FileUtils.list(f, fileList, filter);
                }
            }
        } else {
            boolean accept = filter.accept(file);
            if (file.exists() && accept) {
                fileList.add(file);
            }
        }
        return fileList;
    }

    @Nullable
    public static String getFilename(@Nullable String path) {
        return org.springframework.util.StringUtils.getFilename((String)path);
    }

    @Nullable
    public static String getFileExtension(@Nullable String fullName) {
        return org.springframework.util.StringUtils.getFilenameExtension((String)fullName);
    }

    @Nullable
    public static String getFileExtensionWithDot(@Nullable String fullName) {
        if (fullName == null) {
            return null;
        }
        int extIndex = fullName.lastIndexOf(46);
        if (extIndex == -1) {
            return null;
        }
        int folderIndex = fullName.lastIndexOf(47);
        if (folderIndex > extIndex) {
            return null;
        }
        return fullName.substring(extIndex);
    }

    @Nullable
    public static String getPathWithoutExtension(@Nullable String path) {
        if (path == null) {
            return null;
        }
        return org.springframework.util.StringUtils.stripFilenameExtension((String)path);
    }

    public static String getTempDirPath() {
        return System.getProperty("java.io.tmpdir");
    }

    public static String toTempDirPath(String subDirFile) {
        return FileUtils.toTempDir(subDirFile).getAbsolutePath();
    }

    public static File getTempDir() {
        return new File(FileUtils.getTempDirPath());
    }

    public static File toTempDir(String subDirFile) {
        String fullPath;
        File fullFilePath;
        File dir;
        String tempDirPath = FileUtils.getTempDirPath();
        if (subDirFile.startsWith("/")) {
            subDirFile = subDirFile.substring(1);
        }
        if (!(dir = (fullFilePath = new File(fullPath = tempDirPath.concat(subDirFile))).getParentFile()).exists()) {
            dir.mkdirs();
        }
        return fullFilePath;
    }

    public static String readToString(File file) {
        return FileUtils.readToString(file, StandardCharsets.UTF_8);
    }

    public static String readToString(File file, Charset encoding) {
        String string;
        block8: {
            InputStream in = Files.newInputStream(file.toPath(), new OpenOption[0]);
            try {
                string = IoUtils.readToString(in, encoding);
                if (in == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (in != null) {
                        try {
                            in.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    throw ExceptionUtils.unchecked(e);
                }
            }
            in.close();
        }
        return string;
    }

    public static byte[] readToByteArray(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        }
        catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static void writeToFile(File file, String data) {
        FileUtils.writeToFile(file, data, StandardCharsets.UTF_8, false);
    }

    public static void writeToFile(File file, String data, boolean append) {
        FileUtils.writeToFile(file, data, StandardCharsets.UTF_8, append);
    }

    public static void writeToFile(File file, String data, Charset encoding) {
        FileUtils.writeToFile(file, data, encoding, false);
    }

    public static void writeToFile(File file, String data, Charset encoding, boolean append) {
        try (FileOutputStream out = new FileOutputStream(file, append);){
            IoUtils.write(data, out, encoding);
        }
        catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static void toFile(MultipartFile multipartFile, File file) {
        try {
            FileUtils.toFile(multipartFile.getInputStream(), file);
        }
        catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static void toFile(InputStream in, File file) {
        try (FileOutputStream out = new FileOutputStream(file);){
            FileUtils.copy((InputStream)in, (OutputStream)out);
        }
        catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static void moveFile(File srcFile, File destFile) throws IOException {
        Assert.notNull((Object)srcFile, (String)"Source must not be null", (Object[])new Object[0]);
        Assert.notNull((Object)destFile, (String)"Destination must not be null", (Object[])new Object[0]);
        if (!srcFile.exists()) {
            throw new FileNotFoundException("Source '" + String.valueOf(srcFile) + "' does not exist");
        }
        if (srcFile.isDirectory()) {
            throw new IOException("Source '" + String.valueOf(srcFile) + "' is a directory");
        }
        if (destFile.exists()) {
            throw new IOException("Destination '" + String.valueOf(destFile) + "' already exists");
        }
        if (destFile.isDirectory()) {
            throw new IOException("Destination '" + String.valueOf(destFile) + "' is a directory");
        }
        boolean rename = srcFile.renameTo(destFile);
        if (!rename) {
            FileUtils.copy((File)srcFile, (File)destFile);
            if (!srcFile.delete()) {
                FileUtils.deleteQuietly(destFile);
                throw new IOException("Failed to delete original file '" + String.valueOf(srcFile) + "' after copy to '" + String.valueOf(destFile) + "'");
            }
        }
    }

    public static boolean deleteQuietly(@Nullable File file) {
        if (file == null) {
            return false;
        }
        try {
            if (file.isDirectory()) {
                FileSystemUtils.deleteRecursively((File)file);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            return file.delete();
        }
        catch (Exception ignored) {
            return false;
        }
    }

    public static List<String> readLines(String path) {
        return FileUtils.readLines(Paths.get(path, new String[0]));
    }

    public static List<String> readLines(File file) {
        return FileUtils.readLines(file.toPath());
    }

    public static List<String> readLines(Path path) {
        return FileUtils.readLines(path, StandardCharsets.UTF_8);
    }

    public static List<String> readLines(String path, Charset cs) {
        return FileUtils.readLines(Paths.get(path, new String[0]), cs);
    }

    public static List<String> readLines(File file, Charset cs) {
        return FileUtils.readLines(file.toPath(), cs);
    }

    public static List<String> readLines(Path path, Charset cs) {
        try {
            return Files.readAllLines(path, cs);
        }
        catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static boolean existFile(String filepath) {
        File file = new File(filepath);
        return file.exists();
    }

    public static String getDirectoryPath(String path) {
        File file = new File(path);
        return file.getAbsolutePath();
    }

    public static String getDirectoryPath(Class<?> cls) {
        File file = FileUtils.getJarFile(cls);
        if (file == null) {
            return null;
        }
        if (!file.isDirectory()) {
            file = file.getParentFile();
        }
        return file.getAbsolutePath();
    }

    public static File getJarFile(Class<?> cls) {
        String path = cls.getProtectionDomain().getCodeSource().getLocation().getFile();
        try {
            path = URLDecoder.decode(path, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
        return new File(path);
    }

    public static Boolean createDirectory(String path) {
        File file = new File(path);
        if (!file.isDirectory()) {
            file = file.getParentFile();
        }
        if (!file.exists()) {
            return file.mkdirs();
        }
        return false;
    }

    public static void appendAllText(String path, String contents) {
        try {
            File f = new File(path);
            try (FileWriter fw = new FileWriter(f, true);
                 PrintWriter pw = new PrintWriter(fw);){
                pw.println(contents);
                pw.flush();
                fw.flush();
            }
        }
        catch (IOException exp) {
            throw new BaseException("\u8ffd\u52a0\u6587\u4ef6\u5f02\u5e38", (Throwable)exp);
        }
    }

    public static void writeAllText(String path, String contents) {
        block8: {
            try {
                File f = new File(path);
                if (f.exists()) {
                    f.delete();
                }
                if (!f.createNewFile()) break block8;
                try (BufferedWriter output = new BufferedWriter(new FileWriter(f));){
                    output.write(contents);
                }
            }
            catch (IOException exp) {
                throw new BaseException("\u5199\u6587\u4ef6\u5f02\u5e38", (Throwable)exp);
            }
        }
    }

    public static String readAllText(String path) {
        try {
            File f = new File(path);
            if (f.exists()) {
                long fileLength = f.length();
                byte[] fileContent = new byte[(int)fileLength];
                try (FileInputStream in = new FileInputStream(f);){
                    in.read(fileContent);
                }
                return new String(fileContent);
            }
            throw new FileNotFoundException(path);
        }
        catch (IOException exp) {
            throw new BaseException("\u8bfb\u6587\u4ef6\u5f02\u5e38", (Throwable)exp);
        }
    }

    public static String lineSeparator() {
        return System.getProperty("line.separator");
    }

    public static String getUrl(String url, Integer width, Integer height) {
        return url + "?x-oss-process=style/" + width + "X" + height;
    }

    public static class TrueFilter
    implements FileFilter,
    Serializable {
        private static final long serialVersionUID = -6420452043795072619L;
        public static final TrueFilter TRUE = new TrueFilter();

        @Override
        public boolean accept(File pathname) {
            return true;
        }
    }
}

