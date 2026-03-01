/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.sharding.utils;

import com.kuma.boot.data.mybatis.sharding.annos.Sharding;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

/**
 * 扫描类和注解的工具包
 * @author winjeg
 */
public class ResourceUtil {

    private static final String[] SYSTEM_PATH =
            new String[] {
                    "sun.",
                    "java.",
                    "javax.",
                    "javafx.",
                    "jdk.",
                    "oracle.",
                    "com.sun.",
                    "com.oracle.",
                    "netscape."
            };

    private static final Map<Class<?>, Sharding> SHARDING_MAP = new HashMap<>();

    /**
     * to judge if a class is a system class or not
     *
     * @param path class name full path
     * @return is jdk class or not
     */
    private static boolean isSystemClass(String path) {
        for (String p : SYSTEM_PATH) {
            if (path.startsWith(p)) {
                return true;
            }
        }
        return false;
    }

    public static List<Class<?>> findClasses(String basePackage) {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory =
                new CachingMetadataReaderFactory(resourcePatternResolver);

        List<Class<?>> candidates = new ArrayList<>();
        String packageSearchPath =
                ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                        + resolveBasePackage(basePackage)
                        + "/"
                        + "**/*.class";

        try {
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader =
                            metadataReaderFactory.getMetadataReader(resource);
                    String className = metadataReader.getClassMetadata().getClassName();
                    if (className.isEmpty()
                            || className.contains("$")
                            || isSystemClass(className)) {
                        continue;
                    }
                    candidates.add(Class.forName(className));
                }
            }
        } catch (IOException | ClassNotFoundException e) {

        }
        return candidates;
    }

    public static com.kuma.boot.data.mybatis.sharding.utils.Pair<List<Class<?>>, List<Class<?>>> getClassesWithAnno(String[] packages) {
        List<Class<?>> shardingClasses = new ArrayList<>();
        List<Class<?>> nonShardingClasses = new ArrayList<>();
        for (String pkg : packages) {
            List<Class<?>> cs = findClasses(pkg);
            for (Class<?> clz : cs) {
                for (Annotation a : clz.getAnnotations()) {
                    if (a instanceof Sharding) {
                        if (((Sharding) a).dbRule().isEmpty()
                                && ((Sharding) a).tableRule().isEmpty()) {
                            nonShardingClasses.add(clz);
                        } else {
                            shardingClasses.add(clz);
                        }
                        break;
                    }
                }
            }
        }
        return new com.kuma.boot.data.mybatis.sharding.utils.Pair<>(nonShardingClasses, shardingClasses);
    }

    private static String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(
                SystemPropertyUtils.resolvePlaceholders(basePackage));
    }

    public static Sharding getShardingAnno(Class<?> clz) {
        if (clz == null) {
            return null;
        }
        if (SHARDING_MAP.containsKey(clz)) {
            return SHARDING_MAP.get(clz);
        }
        for (Annotation a : clz.getAnnotations()) {
            if (a instanceof Sharding) {
                SHARDING_MAP.putIfAbsent(clz, (Sharding) a);
                return (Sharding) a;
            }
        }
        return null;
    }
}
