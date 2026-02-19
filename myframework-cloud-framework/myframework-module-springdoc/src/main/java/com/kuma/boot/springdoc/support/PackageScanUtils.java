/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
 *  org.springframework.core.type.classreading.MetadataReader
 *  org.springframework.core.type.classreading.MetadataReaderFactory
 *  org.springframework.core.type.filter.AnnotationTypeFilter
 *  org.springframework.core.type.filter.TypeFilter
 *  org.springframework.web.bind.annotation.RestController
 */
package com.kuma.boot.springdoc.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.web.bind.annotation.RestController;

public class PackageScanUtils {
    public static String[] resolvePackagesWithWildcard(String[] packagesToScan) {
        ArrayList<String> packagesList = new ArrayList<String>(Arrays.asList(packagesToScan));
        for (String packages : packagesToScan) {
            packagesList.addAll(PackageScanUtils.resolvePackagesWithWildcard(packages));
        }
        return packagesList.toArray(new String[0]);
    }

    public static Set<String> resolvePackagesWithWildcard(String packagesToScan) {
        String[] packagePatterns;
        HashSet<String> resolvedPackages = new HashSet<String>();
        for (String pattern : packagePatterns = packagesToScan.split(",")) {
            if ((pattern = pattern.trim()).contains("*")) {
                String basePackage = pattern.substring(0, pattern.indexOf("*")).replace(".*", "");
                resolvedPackages.addAll(PackageScanUtils.scanPackagesWithWildcard(basePackage, pattern));
                continue;
            }
            resolvedPackages.add(pattern);
        }
        return resolvedPackages;
    }

    private static Set<String> scanPackagesWithWildcard(String basePackage, final String pattern) {
        HashSet<String> matchingPackages = new HashSet<String>();
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new TypeFilter(){

            public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                String className = metadataReader.getClassMetadata().getClassName();
                String packageName = className.substring(0, className.lastIndexOf(46));
                String packagePath = packageName.replace('.', '/');
                String patternPath = pattern.replace('.', '/').replace("*", ".*");
                return packagePath.matches(patternPath);
            }
        });
        scanner.addIncludeFilter((TypeFilter)new AnnotationTypeFilter(RestController.class));
        Set classNames = scanner.findCandidateComponents(basePackage).stream().map(BeanDefinition::getBeanClassName).collect(Collectors.toSet());
        for (String className : classNames) {
            String packageName = className.substring(0, className.lastIndexOf(46));
            matchingPackages.add(packageName);
        }
        return matchingPackages;
    }
}

