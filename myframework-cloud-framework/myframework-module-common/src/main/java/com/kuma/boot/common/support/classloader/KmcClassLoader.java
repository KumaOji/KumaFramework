/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.support.classloader;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * KmcClassLoader
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class KmcClassLoader extends ClassLoader {

    public static Class getClassByScn( String className ) {
        Class myclass = null;
        try {
            myclass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            LogUtils.error(e);
            throw new RuntimeException(className + " not found!");
        }
        return myclass;
    }

    /**
     * 获得类的全名，包括包名
     */
    public static String getPackPath( Object object ) {
        // 检查用户传入的参数是否为空
        if (object == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        // 获得类的全名，包括包名
        String clsName = object.getClass().getName();
        return clsName;
    }

    public static String getAppPath( Class cls ) {
        // 检查用户传入的参数是否为空
        if (cls == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        ClassLoader loader = cls.getClassLoader();
        // 获得类的全名，包括包名
        String clsName = cls.getName() + ".class";
        // 获得传入参数所在的包
        Package pack = cls.getPackage();
        String path = "";
        // 如果不是匿名包，将包名转化为路径
        if (pack != null) {
            String packName = pack.getName();
            String javaSpot = "java.";
            String javaxSpot = "jakarta.";
            // 此处简单判定是否是Java基础类库，防止用户传入JDK内置的类库
            if (packName.startsWith(javaSpot) || packName.startsWith(javaxSpot)) {
                throw new IllegalArgumentException("不要传送系统类！");
            }
            // 在类的名称中，去掉包名的部分，获得类的文件名
            clsName = clsName.substring(packName.length() + 1);
            // 判定包名是否是简单包名，如果是，则直接将包名转换为路径，
            if (packName.indexOf(CommonConstants.DOT) < 0) {
                path = packName + "/";
            } else {
                // 否则按照包名的组成部分，将包名转换为路径
                int start = 0, end = 0;
                end = packName.indexOf(".");
                StringBuilder pathBuilder = new StringBuilder();
                while (end != -1) {
                    pathBuilder.append(packName, start, end).append("/");
                    start = end + 1;
                    end = packName.indexOf(".", start);
                }
                if (StringUtils.isNotEmpty(pathBuilder.toString())) {
                    path = pathBuilder.toString();
                }
                path = path + packName.substring(start) + "/";
            }
        }
        // 调用ClassLoader的getResource方法，传入包含路径信息的类文件名
        URL url = loader.getResource(path + clsName);
        // 从URL对象中获取路径信息
        String realPath = url.getPath();
        // 去掉路径信息中的协议名"file:"
        int pos = realPath.indexOf("file:");
        if (pos > -1) {
            realPath = realPath.substring(pos + 5);
        }
        // 去掉路径信息最后包含类文件信息的部分，得到类所在的路径
        pos = realPath.indexOf(path + clsName);
        realPath = realPath.substring(0, pos - 1);
        // 如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
        if (realPath.endsWith(CommonConstants.EXCLAMATORY_MARK)) {
            realPath = realPath.substring(0, realPath.lastIndexOf("/"));
        }
        /*------------------------------------------------------------
         ClassLoader的getResource方法使用了utf-8对路径信息进行了编码，当路径
          中存在中文和空格时，他会对这些字符进行转换，这样，得到的往往不是我们想要
          的真实路径，在此，调用了URLDecoder的decode方法进行解码，以便得到原始的
          中文及空格路径
        -------------------------------------------------------------*/
        try {
            realPath = URLDecoder.decode(realPath, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return realPath;
    } // getAppPath定义结束
}
