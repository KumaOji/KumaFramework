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

package com.kuma.boot.i18n.message;

import com.kuma.boot.common.utils.log.LogUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * 支持 classpath 通配符的 {@link ReloadableResourceBundleMessageSource}.
 *
 * <p>Spring 原生实现对同名文件仅读取第一个，无法聚合多 jar 包中的消息文件。
 * 本实现通过 {@link PathMatchingResourcePatternResolver} 扫描所有匹配文件，
 * 从而支持框架和业务应用在各自 jar 包中提供同名 basename 的消息文件。
 *
 * @author Nicolás Miranda
 * @author hccake
 * @author kuma
 */
public class WildcardReloadableResourceBundleMessageSource
        extends ReloadableResourceBundleMessageSource {

    private static final String PROPERTIES_SUFFIX = ".properties";

    private final PathMatchingResourcePatternResolver resolver =
            new PathMatchingResourcePatternResolver();

    @Override
    protected List<String> calculateAllFilenames(String basename, Locale locale) {
        List<String> filenames = super.calculateAllFilenames(basename, locale);
        // basename 含通配符时从列表中移除，避免 PathMatcher 抛 Illegal char <*> 异常
        if (basename.contains("*")) {
            filenames.remove(basename);
        }
        return filenames;
    }

    @Override
    protected List<String> calculateFilenamesForLocale(String basename, Locale locale) {
        basename = basename.replace(".", "/");
        List<String> fileNames = new ArrayList<>();
        for (String matchFilename : super.calculateFilenamesForLocale(basename, locale)) {
            try {
                Resource[] resources =
                        resolver.getResources("classpath*:" + matchFilename + PROPERTIES_SUFFIX);
                for (Resource resource : resources) {
                    fileNames.add(resource.getURI().toString().replace(PROPERTIES_SUFFIX, ""));
                }
            } catch (IOException ex) {
                LogUtils.error("读取国际化消息文件异常，basename={}", ex, basename);
            }
        }
        return fileNames;
    }
}
