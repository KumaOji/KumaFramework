/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.support.PathMatchingResourcePatternResolver
 *  org.springframework.core.io.support.ResourcePatternResolver
 *  org.springframework.core.type.ClassMetadata
 *  org.springframework.core.type.classreading.MetadataReader
 *  org.springframework.core.type.classreading.SimpleMetadataReaderFactory
 *  org.springframework.util.ClassUtils
 */
package com.kuma.boot.dingtalk.model;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.enums.ExceptionEnum;
import com.kuma.boot.dingtalk.exception.DingerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;

public final class ClassPathScanForResources {
    private static final String CLASSPATH_ALL_URL_PREFIX = "classpath*:";
    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    private static final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    public static Resource[] doScanPackage(String packageSearchPath) {
        try {
            return resolver.getResources(packageSearchPath);
        }
        catch (IOException ex) {
            LogUtils.error((String)packageSearchPath, (Object[])new Object[]{ex});
            throw new DingerException(ExceptionEnum.RESOURCE_CONFIG_EXCEPTION, packageSearchPath);
        }
    }

    public static List<Class<?>> scanInterfaces(String basePackage) {
        return ClassPathScanForResources.scanClasses(basePackage, true);
    }

    public static List<Class<?>> scanClasses(String basePackage) {
        return ClassPathScanForResources.scanClasses(basePackage, false);
    }

    private static List<Class<?>> scanClasses(String basePackage, boolean filterInterface) {
        String packageSearchPath = CLASSPATH_ALL_URL_PREFIX + ClassPathScanForResources.resolveBasePackage(basePackage) + "/**/*.class";
        Resource[] resources = ClassPathScanForResources.doScanPackage(packageSearchPath);
        ArrayList classes = new ArrayList();
        if (resources.length == 0) {
            return classes;
        }
        SimpleMetadataReaderFactory factory = new SimpleMetadataReaderFactory();
        for (Resource resource : resources) {
            String resourceFilename = resource.getFilename();
            if (!resource.isReadable()) {
                LogUtils.debug((String)"Ignored because not readable: {} ", (Object[])new Object[]{resourceFilename});
                continue;
            }
            try {
                MetadataReader metadataReader = factory.getMetadataReader(resource);
                ClassMetadata classMetadata = metadataReader.getClassMetadata();
                Class<?> clazz = Class.forName(classMetadata.getClassName());
                if (filterInterface && !clazz.isInterface()) {
                    LogUtils.debug((String)"source class={} is interface and skip.", (Object[])new Object[]{resourceFilename});
                    continue;
                }
                classes.add(clazz);
            }
            catch (IOException | ClassNotFoundException e) {
                LogUtils.warn((String)"resource={} read exception and message={}.", (Object[])new Object[]{resourceFilename, e.getMessage()});
            }
        }
        return classes;
    }

    private static String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath((String)basePackage);
    }
}

