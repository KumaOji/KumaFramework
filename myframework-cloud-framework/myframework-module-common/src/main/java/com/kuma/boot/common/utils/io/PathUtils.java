/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.jspecify.annotations.Nullable
 */
package com.kuma.boot.common.utils.io;

import com.google.common.collect.Lists;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.utils.collection.CollectionUtils;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.io.UrlUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.jspecify.annotations.Nullable;

public final class PathUtils {
    public static final Path ROOT_PATH = Paths.get("/", new String[0]);

    private PathUtils() {
    }

    public static String getRelativePath(Path parentPath, Path path) {
        String pathStr = path.toString();
        if (ObjectUtils.isNull(parentPath)) {
            return pathStr;
        }
        if (parentPath.toString().length() <= 1) {
            return pathStr;
        }
        String parentPathStr = parentPath.toString();
        if (pathStr.startsWith(parentPathStr)) {
            return pathStr.substring(parentPathStr.length() + 1);
        }
        return pathStr;
    }

    public static Path getPublicParentPath(List<Path> pathList) {
        if (pathList.size() == 1) {
            return PathUtils.getParentPath(pathList.get(0));
        }
        ArrayList<List<String>> pathStrList = new ArrayList<List<String>>(pathList.size());
        for (Path path : pathList) {
            List<String> stringList = CollectionUtils.toStringList(PathUtils.getParentPaths(path));
            pathStrList.add(stringList);
        }
        List<String> publicParentPathStrs = PathUtils.retainAll(pathStrList);
        String maxLengthParent = PathUtils.getMaxLength(publicParentPathStrs);
        return Paths.get(maxLengthParent, new String[0]);
    }

    private static String getMaxLength(List<String> stringList) {
        String result = "";
        for (String string : stringList) {
            if (string.length() <= result.length()) continue;
            result = string;
        }
        return result;
    }

    public static List<Path> getParentPaths(Path path) {
        if (ObjectUtils.isNull(path)) {
            return Collections.emptyList();
        }
        ArrayList<Path> pathList = new ArrayList<Path>();
        Path parentPath = path.getParent();
        while (ObjectUtils.isNotNull(parentPath)) {
            pathList.add(parentPath);
            parentPath = parentPath.getParent();
        }
        if (CollectionUtils.isEmpty(pathList)) {
            pathList.add(ROOT_PATH);
        }
        return pathList;
    }

    public static Path getParentPath(Path path) {
        Path parentPath = path.getParent();
        if (ObjectUtils.isNull(parentPath)) {
            return ROOT_PATH;
        }
        return parentPath;
    }

    public static List<String> retainAll(List<List<String>> collectionList) {
        if (CollectionUtils.isEmpty(collectionList)) {
            return Collections.emptyList();
        }
        if (collectionList.size() == 1) {
            return collectionList.get(0);
        }
        List<String> result = collectionList.get(0);
        for (int i = 1; i < collectionList.size(); ++i) {
            result.retainAll((Collection)collectionList.get(i));
        }
        return result;
    }

    public static List<Path> getPathList(Path rootPath) {
        final ArrayList<Path> pathList = new ArrayList<Path>();
        try {
            if (Files.isDirectory(rootPath, new LinkOption[0])) {
                Files.walkFileTree(rootPath, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        pathList.add(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        pathList.add(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } else {
                pathList.add(rootPath);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return pathList;
    }

    public static List<Path> getPathList(String dir, String glob) {
        LinkedList<Path> list = new LinkedList<Path>();
        Path root = Paths.get(dir, new String[0]);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root, glob);){
            for (Path path : stream) {
                list.add(path);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static List<Path> getDirFileNames(String dir, String glob) {
        LinkedList<Path> list = new LinkedList<Path>();
        Path root = Paths.get(dir, new String[0]);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root, glob);){
            for (Path path : stream) {
                list.add(path.getFileName());
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static List<Path> getAllDirFileNames(String dir) {
        return PathUtils.getDirFileNames(dir, "*.*");
    }

    public static List<String> getDirFileNameStrs(String dir, String glob) {
        LinkedList<String> list = new LinkedList<String>();
        Path root = Paths.get(dir, new String[0]);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root, glob);){
            for (Path path : stream) {
                list.add(path.getFileName().toString());
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Deprecated
    public static String getPath() {
        return System.getProperty("user.dir");
    }

    public static String getRootPath() {
        return Class.class.getClass().getResource("/").getPath();
    }

    public static String getAppRootPath() {
        File emptyFile = new File("");
        return emptyFile.getAbsolutePath();
    }

    public static String getAppResourcesPath() {
        return PathUtils.getAppRootPath() + "/src/main/resources";
    }

    public static String getAppTestResourcesPath() {
        return PathUtils.getAppRootPath() + "/src/test/resources";
    }

    public static String getRootPath(Class clazz) {
        String uriPath = clazz.getResource("/").toString();
        return uriPath.replace("filelist:", "").replace("target/classes/", "src/main/java/");
    }

    public static String getPath(Class clazz) {
        String uriPath = clazz.getResource("").toString();
        return uriPath.replace("filelist:", "").replace("target/classes/", "src/main/java/");
    }

    public static String packageToPath(String packagePath) {
        return packagePath.replaceAll("\\.", "/");
    }

    public static List<String> readAllLines(String pathStr) {
        return PathUtils.readAllLines(pathStr, "UTF-8");
    }

    public static List<String> readAllLines(String pathStr, String charset) {
        return PathUtils.readAllLines(pathStr, charset, 0, Integer.MAX_VALUE);
    }

    public static List<String> readAllLines(String pathStr, String charset, int startIndex, int endIndex) {
        ArgUtils.notEmpty(pathStr, "pathStr");
        ArgUtils.notEmpty(charset, "charset");
        ArgUtils.assertTrue(endIndex >= startIndex, "endIndex >= startIndex");
        Path path = Paths.get(pathStr, new String[0]);
        try {
            List<String> allLines = Files.readAllLines(path, Charset.forName(charset));
            int size = allLines.size();
            if (endIndex > size) {
                endIndex = size;
            }
            return allLines.subList(startIndex, endIndex);
        }
        catch (IOException e) {
            throw new BootException(e);
        }
    }

    public static void writeLines(String pathStr, String ... lines) {
        ArrayList stringList = Lists.newArrayList((Object[])lines);
        PathUtils.writeLines(pathStr, stringList, "UTF-8", new OpenOption[0]);
    }

    public static void writeLines(String pathStr, Collection<String> lines) {
        PathUtils.writeLines(pathStr, lines, "UTF-8", new OpenOption[0]);
    }

    public static void appendLines(String pathStr, String ... lines) {
        ArrayList stringList = Lists.newArrayList((Object[])lines);
        PathUtils.writeLines(pathStr, stringList, "UTF-8", StandardOpenOption.APPEND);
    }

    public static void appendLines(String pathStr, Collection<String> lines) {
        PathUtils.writeLines(pathStr, lines, "UTF-8", StandardOpenOption.APPEND);
    }

    public static void writeLines(String pathStr, Collection<String> lines, String charset, OpenOption ... openOptions) {
        ArgUtils.notEmpty(pathStr, "pathStr");
        ArgUtils.notEmpty(charset, "charset");
        ArgUtils.notEmpty(lines, "lines");
        try {
            Path path = Paths.get(pathStr, new String[0]);
            Files.write(path, lines, Charset.forName(charset), openOptions);
        }
        catch (IOException e) {
            throw new BootException(e);
        }
    }

    public static @Nullable String getJarPath() {
        try {
            URL url = PathUtils.class.getResource("/").toURI().toURL();
            return PathUtils.toFilePath(url);
        }
        catch (Exception e) {
            String path = PathUtils.class.getResource("").getPath();
            return new File(path).getParentFile().getParentFile().getAbsolutePath();
        }
    }

    private static @Nullable String toFilePath(@Nullable URL url) {
        if (url == null) {
            return null;
        }
        String protocol = url.getProtocol();
        String file = UrlUtils.decode((String)url.getPath(), (Charset)StandardCharsets.UTF_8);
        if ("file".equals(protocol)) {
            return new File(file).getParentFile().getParentFile().getAbsolutePath();
        }
        if ("jar".equals(protocol) || "zip".equals(protocol)) {
            int ipos = file.indexOf("!/");
            if (ipos > 0) {
                file = file.substring(0, ipos);
            }
            if (file.startsWith("file:")) {
                file = file.substring("file:".length());
            }
            return new File(file).getParentFile().getAbsolutePath();
        }
        return file;
    }
}

