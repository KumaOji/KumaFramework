/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package com.kuma.boot.common.utils.io;

import com.google.common.collect.Sets;
import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.utils.collection.ArrayUtils;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class PackageUtils {
    public static String getPackageName(Class clazz) {
        return clazz.getPackage().getName();
    }

    public static String getSlimPackageName(String fullPackageName) {
        if (StringUtils.isEmpty(fullPackageName)) {
            return fullPackageName;
        }
        String[] strings = fullPackageName.split("\\.");
        ArrayList<Object> newList = new ArrayList<Object>(strings.length);
        for (int i = 0; i < strings.length - 1; ++i) {
            String text = strings[i];
            String firstChar = "" + text.charAt(0);
            newList.add(firstChar);
        }
        newList.add(strings[strings.length - 1]);
        return StringUtils.join(newList, ".");
    }

    public static boolean isSamePackage(String packageName, Class clazz) {
        String targetPackage = PackageUtils.getPackageName(clazz);
        return packageName.equals(targetPackage);
    }

    public static boolean isJavaLangPackage(Class clazz) {
        String packageName = PackageUtils.getPackageName(clazz);
        return "java.lang".equals(packageName);
    }

    public static Set<String> scanPackageClassNameSet(String packageName) {
        ArgUtils.notEmpty(packageName, "packageNames");
        HashSet classNameSet = Sets.newHashSet();
        String packageDirName = packageName.replace('.', '/');
        try {
            Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    Object[] files;
                    String filePath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8);
                    File file = new File(filePath);
                    if (!file.isDirectory() || !ArrayUtils.isNotEmpty(files = file.listFiles())) continue;
                    for (Object entry : files) {
                        PackageUtils.recursiveFile(packageName, (File)entry, classNameSet);
                    }
                    continue;
                }
                if ("jar".equals(protocol)) {
                    JarURLConnection jarURLConnection = (JarURLConnection)url.openConnection();
                    JarFile jarFile = jarURLConnection.getJarFile();
                    Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
                    jarEntryEnumeration.nextElement();
                    while (jarEntryEnumeration.hasMoreElements()) {
                        JarEntry jarEntry = jarEntryEnumeration.nextElement();
                        jarEntry.isDirectory();
                        LogUtils.info("jar " + jarEntry.getName(), new Object[0]);
                    }
                    continue;
                }
                System.err.println("Not support protocol: " + protocol);
            }
        }
        catch (IOException e) {
            throw new BootException(e);
        }
        return classNameSet;
    }

    private static void recursiveFile(String packageNamePrefix, File file, Set<String> classNameSet) {
        if (file.isFile()) {
            String fileName = file.getName().split("\\.")[0];
            String className = (String)packageNamePrefix + "." + fileName;
            classNameSet.add(className);
        } else if (file.isDirectory()) {
            Object[] files = file.listFiles();
            String dirName = file.getName();
            packageNamePrefix = (String)packageNamePrefix + "." + dirName;
            if (ArrayUtils.isNotEmpty(files)) {
                for (Object fileEntry : files) {
                    PackageUtils.recursiveFile((String)packageNamePrefix, (File)fileEntry, classNameSet);
                }
            }
        }
    }
}

