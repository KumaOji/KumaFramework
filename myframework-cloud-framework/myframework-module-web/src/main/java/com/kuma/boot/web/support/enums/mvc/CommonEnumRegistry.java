/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.kuma.boot.common.enums.base.CommonEnum
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.log.LogUtils
 *  jakarta.annotation.PostConstruct
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.io.support.ResourcePatternResolver
 *  org.springframework.core.io.support.ResourcePatternUtils
 *  org.springframework.core.type.ClassMetadata
 *  org.springframework.core.type.classreading.MetadataReader
 *  org.springframework.core.type.classreading.SimpleMetadataReaderFactory
 *  org.springframework.stereotype.Component
 */
package com.kuma.boot.web.support.enums.mvc;

import com.google.common.collect.Maps;
import com.kuma.boot.common.enums.base.CommonEnum;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.stereotype.Component;

@Component
public class CommonEnumRegistry {
    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    private static final String BASE__ENUM_CLASS_NAME = CommonEnum.class.getName();
    private final Map<String, List<CommonEnum>> nameDict = Maps.newLinkedHashMap();
    private final Map<Class<?>, List<CommonEnum>> classDict = Maps.newLinkedHashMap();
    @Value(value="${baseEnum.basePackage:''}")
    private String basePackage;
    @Autowired
    private ResourceLoader resourceLoader;

    @PostConstruct
    public void initDict() {
        if (StringUtils.isEmpty((String)this.basePackage)) {
            return;
        }
        ResourcePatternResolver resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver((ResourceLoader)this.resourceLoader);
        SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        try {
            Resource[] resources;
            String pkg = this.toPackage(this.basePackage);
            String packageSearchPath = "classpath*:" + pkg + DEFAULT_RESOURCE_PATTERN;
            for (Resource resource : resources = resourcePatternResolver.getResources(packageSearchPath)) {
                if (!resource.isReadable()) continue;
                try {
                    String className;
                    Class<?> cls;
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    ClassMetadata classMetadata = metadataReader.getClassMetadata();
                    String[] interfaceNames = classMetadata.getInterfaceNames();
                    if (!Arrays.asList(interfaceNames).contains(BASE__ENUM_CLASS_NAME) || !(cls = Class.forName(className = classMetadata.getClassName())).isEnum() || !CommonEnum.class.isAssignableFrom(cls)) continue;
                    ?[] enumConstants = cls.getEnumConstants();
                    List<CommonEnum> commonEnums = Arrays.stream(enumConstants).filter(e -> e instanceof CommonEnum).map(e -> (CommonEnum)e).toList();
                    String key = this.convertKeyFromClassName(cls.getSimpleName());
                    this.nameDict.put(key, commonEnums);
                    this.classDict.put(cls, commonEnums);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
        }
        catch (IOException e2) {
            LogUtils.error((String)"failed to load dict by auto register", (Object[])new Object[]{e2});
        }
    }

    private String toPackage(String basePackage) {
        String result = basePackage.replace(".", "/");
        return result + "/";
    }

    private String convertKeyFromClassName(String className) {
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }

    public Map<String, List<CommonEnum>> getNameDict() {
        return this.nameDict;
    }

    public Map<Class<?>, List<CommonEnum>> getClassDict() {
        return this.classDict;
    }

    public String getBasePackage() {
        return this.basePackage;
    }

    public ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }
}

