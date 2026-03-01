/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.support.PathMatchingResourcePatternResolver
 */
package com.kuma.boot.dingtalk.utils;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.spring.ApplicationHome;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class PackageUtils {
    private static final ApplicationHome applicationHome = new ApplicationHome();
    public static final String SPOT = ".";
    public static final String SLANT = "/";
    private static final String JAR_FILE_SUFFIX = ".jar";
    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    private PackageUtils() {
    }

    public static void classNames(String packageName, List<Class<?>> classNames, boolean isInterface, Class<? extends Annotation> ... filterAnnotations) {
        String absolutePath;
        if (DingerUtils.isEmpty(packageName)) {
            return;
        }
        File applicationHomeSource = applicationHome.getSource();
        if (applicationHomeSource != null && (absolutePath = applicationHomeSource.getAbsolutePath()).endsWith(JAR_FILE_SUFFIX)) {
            PackageUtils.jarClassNames(absolutePath, packageName, classNames, isInterface, filterAnnotations);
            return;
        }
        PackageUtils.forClassNames(packageName, classNames, isInterface, filterAnnotations);
    }

    public static void forClassNames(String packageName, List<Class<?>> classNames, boolean isInterface, Class<? extends Annotation> ... filterAnnotations) {
        List<String> repeatCheck = classNames.stream().map(Class::getName).toList();
        try {
            File[] files;
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL url = classLoader.getResource(packageName.replace(SPOT, SLANT));
            if (url == null) {
                LogUtils.debug((String)"packageName={} is not exist.", (Object[])new Object[]{packageName});
                return;
            }
            URI uri = url.toURI();
            File file = new File(uri);
            block2: for (File f : files = file.listFiles()) {
                String name = f.getName();
                if (f.isFile()) {
                    boolean check;
                    String className = packageName + SPOT + name.substring(0, name.lastIndexOf(SPOT));
                    Class<?> clazz = classLoader.loadClass(className);
                    boolean bl = check = !isInterface || clazz.isInterface();
                    if (check) {
                        if (filterAnnotations.length > 0) {
                            for (Class<? extends Annotation> annotation : filterAnnotations) {
                                if (!clazz.isAnnotationPresent(annotation)) continue;
                                if (repeatCheck.contains(className)) continue block2;
                                classNames.add(clazz);
                                continue block2;
                            }
                            continue;
                        }
                        if (repeatCheck.contains(className)) continue;
                        classNames.add(clazz);
                        continue;
                    }
                    LogUtils.debug((String)"skip class {}.", (Object[])new Object[]{clazz.getName()});
                    continue;
                }
                PackageUtils.forClassNames(packageName + SPOT + name, classNames, isInterface, filterAnnotations);
            }
        }
        catch (Exception ex) {
            LogUtils.error((String)"when analysis packageName={} catch exception=", (Object[])new Object[]{packageName, ex});
        }
    }

    public static void jarClassNames(String jarPath, String packageName, List<Class<?>> classNames, boolean isInterface, Class<? extends Annotation> ... filterAnnotations) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        List<String> repeatCheck = classNames.stream().map(Class::getName).toList();
        packageName = packageName.replace(SPOT, SLANT);
        try {
            JarFile jarFile = new JarFile(jarPath);
            Enumeration<JarEntry> entries = jarFile.entries();
            block2: while (entries.hasMoreElements()) {
                boolean check;
                JarEntry jarEntry = entries.nextElement();
                String namePath = jarEntry.getName();
                if (!namePath.contains(packageName) || !namePath.endsWith(".class")) continue;
                namePath = namePath.substring(namePath.indexOf(packageName));
                String className = namePath.replaceAll(SLANT, SPOT).replace(".class", "");
                Class<?> clazz = classLoader.loadClass(className);
                boolean bl = check = isInterface ? clazz.isInterface() : true;
                if (check) {
                    if (filterAnnotations.length > 0) {
                        for (Class<? extends Annotation> annotation : filterAnnotations) {
                            if (!clazz.isAnnotationPresent(annotation)) continue;
                            if (repeatCheck.contains(className)) continue block2;
                            classNames.add(clazz);
                            continue block2;
                        }
                        continue;
                    }
                    if (repeatCheck.contains(className)) continue;
                    classNames.add(clazz);
                    continue;
                }
                LogUtils.debug((String)"skip class {}.", (Object[])new Object[]{clazz.getName()});
            }
        }
        catch (Exception ex) {
            LogUtils.error((String)"when analysis packageName={} catch exception=", (Object[])new Object[]{packageName, ex});
        }
    }

    private static void doScan(String packageName, List<Class<?>> classNames, Class<? extends Annotation> ... filterAnnotations) {
        List<String> repeatCheck = classNames.stream().map(Class::getName).toList();
        String packageSearchPath = "classpath*:" + packageName.replace(SPOT, SLANT) + "/**/*.class";
        try {
            Resource[] resources;
            block2: for (Resource resource : resources = new PathMatchingResourcePatternResolver().getResources(packageSearchPath)) {
                File f = resource.getFile();
                String name = f.getName();
                if (f.isFile()) {
                    String className = packageName + SPOT + name.substring(0, name.lastIndexOf(SPOT));
                    Class<?> clazz = Class.forName(className);
                    if (filterAnnotations.length > 0) {
                        for (Class<? extends Annotation> annotation : filterAnnotations) {
                            if (!clazz.isAnnotationPresent(annotation)) continue;
                            if (repeatCheck.contains(className)) continue block2;
                            classNames.add(clazz);
                            continue block2;
                        }
                        continue;
                    }
                    if (repeatCheck.contains(className)) continue;
                    classNames.add(clazz);
                    continue;
                }
                PackageUtils.doScan(packageName + SPOT + name, classNames, filterAnnotations);
            }
        }
        catch (Exception ex) {
            LogUtils.error((String)"when analysis packageName={} catch exception=", (Object[])new Object[]{packageName, ex});
        }
    }
}

