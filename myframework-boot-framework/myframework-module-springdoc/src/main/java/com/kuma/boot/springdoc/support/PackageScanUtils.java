package com.kuma.boot.springdoc.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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

    public static String[] resolvePackagesWithWildcard(String[] packagesToScan){
        List<String> packagesList = new ArrayList<>(Arrays.asList(packagesToScan));
        for (String packages :packagesToScan){
            packagesList.addAll(resolvePackagesWithWildcard(packages));
        }
        return packagesList.toArray(new String[0]);
    }
    /**
     * 解析包含通配符的包名
     */
    public static Set<String> resolvePackagesWithWildcard(String packagesToScan) {
        Set<String> resolvedPackages = new HashSet<>();
        String[] packagePatterns = packagesToScan.split(",");

        for (String pattern : packagePatterns) {
            pattern = pattern.trim();
            if (pattern.contains("*")) {
                // 处理通配符
                String basePackage = pattern.substring(0, pattern.indexOf("*")).replace(".*", "");
                resolvedPackages.addAll(scanPackagesWithWildcard(basePackage, pattern));
            } else {
                // 普通包名
                resolvedPackages.add(pattern);
            }
        }

        return resolvedPackages;
    }

    private static Set<String> scanPackagesWithWildcard(String basePackage, String pattern) {
        Set<String> matchingPackages = new HashSet<>();

        // 创建一个不使用默认过滤器的扫描器
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

        // 添加自定义过滤器来匹配类
        scanner.addIncludeFilter(new TypeFilter() {
            @Override
            public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
                    throws IOException {
                String className = metadataReader.getClassMetadata().getClassName();
                String packageName = className.substring(0, className.lastIndexOf('.'));

                // 将包名转换为路径格式进行匹配
                String packagePath = packageName.replace('.', '/');
                String patternPath = pattern.replace('.', '/').replace("*", ".*");

                return packagePath.matches(patternPath);
            }
        });

        // 添加RestController过滤器
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));

        // 扫描类
        Set<String> classNames = scanner.findCandidateComponents(basePackage).stream()
                .map(BeanDefinition::getBeanClassName)
                .collect(Collectors.toSet());

        // 提取包名
        for (String className : classNames) {
            String packageName = className.substring(0, className.lastIndexOf('.'));
            matchingPackages.add(packageName);
        }

        return matchingPackages;
    }
}
