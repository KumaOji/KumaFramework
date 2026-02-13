/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.core.io.ClassPathResource
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.support.PathMatchingResourcePatternResolver
 *  org.springframework.util.ResourceUtils
 */
package com.kuma.boot.common.utils.io;

import com.kuma.boot.common.utils.log.LogUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class ResourceUtils {
    private static volatile ResourceUtils INSTANCE;
    private final PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();

    private ResourceUtils() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static ResourceUtils getInstance() {
        if (!ObjectUtils.isEmpty((Object)INSTANCE)) return INSTANCE;
        Class<ResourceUtils> clazz = ResourceUtils.class;
        synchronized (ResourceUtils.class) {
            if (!ObjectUtils.isEmpty((Object)INSTANCE)) return INSTANCE;
            INSTANCE = new ResourceUtils();
            // ** MonitorExit[var0] (shouldn't be in output)
            return INSTANCE;
        }
    }

    private PathMatchingResourcePatternResolver getPathMatchingResourcePatternResolver() {
        return this.pathMatchingResourcePatternResolver;
    }

    private static PathMatchingResourcePatternResolver getResolver() {
        return ResourceUtils.getInstance().getPathMatchingResourcePatternResolver();
    }

    public static Resource getResource(String location) {
        return ResourceUtils.getResolver().getResource(location);
    }

    public static File getFile(String location) throws IOException {
        return ResourceUtils.getResource(location).getFile();
    }

    public static InputStream getInputStream(String location) throws IOException {
        return ResourceUtils.getResource(location).getInputStream();
    }

    public static String getFilename(String location) {
        return ResourceUtils.getResource(location).getFilename();
    }

    public static URI getURI(String location) throws IOException {
        return ResourceUtils.getResource(location).getURI();
    }

    public static URL getURL(String location) throws IOException {
        return ResourceUtils.getResource(location).getURL();
    }

    public static long contentLength(String location) throws IOException {
        return ResourceUtils.getResource(location).contentLength();
    }

    public static long lastModified(String location) throws IOException {
        return ResourceUtils.getResource(location).lastModified();
    }

    public static boolean exists(String location) {
        return ResourceUtils.getResource(location).exists();
    }

    public static boolean isFile(String location) {
        return ResourceUtils.getResource(location).isFile();
    }

    public static boolean isReadable(String location) {
        return ResourceUtils.getResource(location).isReadable();
    }

    public static boolean isOpen(String location) {
        return ResourceUtils.getResource(location).isOpen();
    }

    public static Resource[] getResources(String locationPattern) throws IOException {
        return ResourceUtils.getResolver().getResources(locationPattern);
    }

    public static boolean isUrl(String location) {
        return org.springframework.util.ResourceUtils.isUrl((String)location);
    }

    public static boolean isClasspathUrl(String location) {
        return StringUtils.startsWith((CharSequence)location, (CharSequence)"classpath:");
    }

    public static boolean isClasspathAllUrl(String location) {
        return StringUtils.startsWith((CharSequence)location, (CharSequence)"classpath*:");
    }

    public static boolean isJarUrl(URL url) {
        return org.springframework.util.ResourceUtils.isJarURL((URL)url);
    }

    public static boolean isFileUrl(URL url) {
        return org.springframework.util.ResourceUtils.isFileURL((URL)url);
    }

    public static void getFileContent(Object fileInPath) throws IOException {
        String line;
        BufferedReader br = null;
        if (fileInPath == null) {
            return;
        }
        if (fileInPath instanceof String) {
            br = new BufferedReader(new FileReader((String)fileInPath));
        } else if (fileInPath instanceof InputStream) {
            br = new BufferedReader(new InputStreamReader((InputStream)fileInPath));
        }
        while ((line = br.readLine()) != null) {
            LogUtils.info(line, new Object[0]);
        }
        br.close();
    }

    public void function1(String fileName) throws IOException {
        String path = this.getClass().getClassLoader().getResource(fileName).getPath();
        LogUtils.info(path, new Object[0]);
        String filePath = path + fileName;
        LogUtils.info(filePath, new Object[0]);
        ResourceUtils.getFileContent(filePath);
    }

    public void function2(String fileName) throws IOException {
        String path = this.getClass().getClassLoader().getResource(fileName).getPath();
        LogUtils.info(path, new Object[0]);
        String filePath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        LogUtils.info(filePath, new Object[0]);
        ResourceUtils.getFileContent(filePath);
    }

    public void function3(String fileName) throws IOException {
        String path = this.getClass().getClassLoader().getResource(fileName).getFile();
        LogUtils.info(path, new Object[0]);
        String filePath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        LogUtils.info(filePath, new Object[0]);
        ResourceUtils.getFileContent(filePath);
    }

    public void function4(String fileName) throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
        ResourceUtils.getFileContent(in);
    }

    public void function5(String fileName) throws IOException {
        InputStream in = this.getClass().getResourceAsStream("/" + fileName);
        ResourceUtils.getFileContent(in);
    }

    public void function6(String fileName) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(fileName);
        InputStream inputStream = classPathResource.getInputStream();
        ResourceUtils.getFileContent(inputStream);
    }

    public void function7(String fileName) throws IOException {
        String rootPath = System.getProperty("user.dir");
        String filePath = rootPath + "\\chapter-2-springmvc-quickstart\\src\\main\\resources\\" + fileName;
        ResourceUtils.getFileContent(filePath);
    }

    public void function8(String fileName) throws IOException {
        File directory = new File("");
        String rootCanonicalPath = directory.getCanonicalPath();
        String rootAbsolutePath = directory.getAbsolutePath();
        LogUtils.info(rootCanonicalPath, new Object[0]);
        LogUtils.info(rootAbsolutePath, new Object[0]);
        String filePath = rootCanonicalPath + "\\chapter-2-springmvc-quickstart\\src\\main\\resources\\" + fileName;
        ResourceUtils.getFileContent(filePath);
    }

    public void function9(String fileName) throws IOException {
        System.setProperty("TEST_ROOT", "E:\\WorkSpace\\Git\\spring-framework-learning-example");
        String rootPath = System.getProperty("TEST_ROOT");
        LogUtils.info(rootPath, new Object[0]);
        String filePath = rootPath + "\\chapter-2-springmvc-quickstart\\src\\main\\resources\\" + fileName;
        ResourceUtils.getFileContent(filePath);
    }
}

