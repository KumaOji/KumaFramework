/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.context.support.ReloadableResourceBundleMessageSource
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.support.PathMatchingResourcePatternResolver
 */
package com.kuma.boot.web.i18n;

import com.kuma.boot.common.utils.log.LogUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class WildcardReloadableResourceBundleMessageSource
extends ReloadableResourceBundleMessageSource {
    private static final String PROPERTIES_SUFFIX = ".properties";
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    protected List<String> calculateAllFilenames(String basename, Locale locale) {
        List filenames = super.calculateAllFilenames(basename, locale);
        if (basename.contains("*")) {
            filenames.remove(basename);
        }
        return filenames;
    }

    protected List<String> calculateFilenamesForLocale(String basename, Locale locale) {
        basename = basename.replace(".", "/");
        ArrayList<String> fileNames = new ArrayList<String>();
        List matchFilenames = super.calculateFilenamesForLocale(basename, locale);
        for (String matchFilename : matchFilenames) {
            try {
                Resource[] resources;
                for (Resource resource : resources = this.resolver.getResources("classpath*:" + matchFilename + PROPERTIES_SUFFIX)) {
                    String sourcePath = resource.getURI().toString().replace(PROPERTIES_SUFFIX, "");
                    fileNames.add(sourcePath);
                }
            }
            catch (IOException ex) {
                LogUtils.error((String)"\u8bfb\u53d6\u56fd\u9645\u5316\u4fe1\u606f\u6587\u4ef6\u5f02\u5e38", (Object[])new Object[]{ex});
            }
        }
        return fileNames;
    }
}

