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

package com.kuma.boot.web.support.enums.mvc;

import com.google.common.collect.Maps;
import com.kuma.boot.common.enums.base.CommonEnum;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class CommonEnumRegistry {

    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    private static final String BASE__ENUM_CLASS_NAME = CommonEnum.class.getName();
    private final Map<String, List<CommonEnum>> nameDict = Maps.newLinkedHashMap();

    private final Map<Class<?>, List<CommonEnum>> classDict = Maps.newLinkedHashMap();

    @Value("${baseEnum.basePackage:''}")
    private String basePackage;

    @Autowired private ResourceLoader resourceLoader;

    @PostConstruct
    public void initDict() {
        if (StringUtils.isEmpty(basePackage)) {
            return;
        }
        ResourcePatternResolver resourcePatternResolver =
                ResourcePatternUtils.getResourcePatternResolver(this.resourceLoader);
        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        try {
            String pkg = toPackage(this.basePackage);
            // 对 basePackage 包进行扫描
            String packageSearchPath =
                    ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                            + pkg
                            + DEFAULT_RESOURCE_PATTERN;
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    try {
                        MetadataReader metadataReader =
                                metadataReaderFactory.getMetadataReader(resource);
                        ClassMetadata classMetadata = metadataReader.getClassMetadata();

                        String[] interfaceNames = classMetadata.getInterfaceNames();
                        // 实现 BASE_ENUM_CLASS_NAME 接口
                        if (Arrays.asList(interfaceNames).contains(BASE__ENUM_CLASS_NAME)) {
                            String className = classMetadata.getClassName();

                            // 加载 cls
                            Class<?> cls = Class.forName(className);
                            if (cls.isEnum() && CommonEnum.class.isAssignableFrom(cls)) {
                                Object[] enumConstants = cls.getEnumConstants();
                                List<CommonEnum> commonEnums =
                                        Arrays.stream(enumConstants)
                                                .filter(e -> e instanceof CommonEnum)
                                                .map(e -> (CommonEnum) e)
                                                .toList();

                                String key = convertKeyFromClassName(cls.getSimpleName());

                                this.nameDict.put(key, commonEnums);
                                this.classDict.put(cls, commonEnums);
                            }
                        }
                    } catch (Throwable ex) {
                        // ignore
                    }
                }
            }
        } catch (IOException e) {
            LogUtils.error("failed to load dict by auto register", e);
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
        return nameDict;
    }

    public Map<Class<?>, List<CommonEnum>> getClassDict() {
        return classDict;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
