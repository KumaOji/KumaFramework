/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.classloader;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class KmcClassLoader
extends ClassLoader {
    public static Class getClassByScn(String className) {
        Class<?> myclass = null;
        try {
            myclass = Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            LogUtils.error(e);
            throw new RuntimeException(className + " not found!");
        }
        return myclass;
    }

    public static String getPackPath(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("\u53c2\u6570\u4e0d\u80fd\u4e3a\u7a7a\uff01");
        }
        String clsName = object.getClass().getName();
        return clsName;
    }

    public static String getAppPath(Class cls) {
        URL url;
        String realPath;
        int pos;
        if (cls == null) {
            throw new IllegalArgumentException("\u53c2\u6570\u4e0d\u80fd\u4e3a\u7a7a\uff01");
        }
        ClassLoader loader = cls.getClassLoader();
        Object clsName = cls.getName() + ".class";
        Package pack = cls.getPackage();
        Object path = "";
        if (pack != null) {
            String packName = pack.getName();
            String javaSpot = "java.";
            String javaxSpot = "jakarta.";
            if (packName.startsWith(javaSpot) || packName.startsWith(javaxSpot)) {
                throw new IllegalArgumentException("\u4e0d\u8981\u4f20\u9001\u7cfb\u7edf\u7c7b\uff01");
            }
            clsName = ((String)clsName).substring(packName.length() + 1);
            if (packName.indexOf(46) < 0) {
                path = packName + "/";
            } else {
                int start = 0;
                int end = 0;
                end = packName.indexOf(".");
                StringBuilder pathBuilder = new StringBuilder();
                while (end != -1) {
                    pathBuilder.append(packName, start, end).append("/");
                    start = end + 1;
                    end = packName.indexOf(".", start);
                }
                if (StringUtils.isNotEmpty((CharSequence)pathBuilder.toString())) {
                    path = pathBuilder.toString();
                }
                path = (String)path + packName.substring(start) + "/";
            }
        }
        if ((pos = (realPath = (url = loader.getResource((String)path + (String)clsName)).getPath()).indexOf("file:")) > -1) {
            realPath = realPath.substring(pos + 5);
        }
        if ((realPath = realPath.substring(0, (pos = realPath.indexOf((String)path + (String)clsName)) - 1)).endsWith("!")) {
            realPath = realPath.substring(0, realPath.lastIndexOf("/"));
        }
        try {
            realPath = URLDecoder.decode(realPath, StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return realPath;
    }
}

