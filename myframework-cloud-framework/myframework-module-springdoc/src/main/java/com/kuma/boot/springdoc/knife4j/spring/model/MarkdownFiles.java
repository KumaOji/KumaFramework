/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.github.xiaoymin.knife4j.core.extend.OpenApiExtendMarkdownChildren
 *  com.github.xiaoymin.knife4j.core.util.CommonUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.support.PathMatchingResourcePatternResolver
 *  org.springframework.core.io.support.ResourcePatternResolver
 */
package com.kuma.boot.springdoc.knife4j.spring.model;

import com.github.xiaoymin.knife4j.core.extend.OpenApiExtendMarkdownChildren;
import com.github.xiaoymin.knife4j.core.util.CommonUtils;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class MarkdownFiles {
    private static final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
    Logger logger = LoggerFactory.getLogger(MarkdownFiles.class);
    private String basePath;
    private List<OpenApiExtendMarkdownChildren> markdownFiles = new ArrayList<OpenApiExtendMarkdownChildren>();

    public List<OpenApiExtendMarkdownChildren> getMarkdownFiles() {
        return this.markdownFiles;
    }

    public void setMarkdownFiles(List<OpenApiExtendMarkdownChildren> markdownFiles) {
        this.markdownFiles = markdownFiles;
    }

    public String getBasePath() {
        return this.basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public MarkdownFiles() {
    }

    public MarkdownFiles(String basePath) {
        this.basePath = basePath;
    }

    public void init() {
        if (this.basePath != null && this.basePath != "" && !"".equals(this.basePath)) {
            try {
                Resource[] resources = resourceResolver.getResources(this.basePath);
                if (resources != null && resources.length > 0) {
                    for (Resource resource : resources) {
                        OpenApiExtendMarkdownChildren markdownFile = this.createMarkdownFile(resource);
                        if (markdownFile == null) continue;
                        this.getMarkdownFiles().add(markdownFile);
                    }
                }
            }
            catch (Exception e) {
                this.logger.warn("(Ignores) Failed to read Markdown files,Error Message:{} ", (Object)e.getMessage());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private OpenApiExtendMarkdownChildren createMarkdownFile(Resource resource) {
        OpenApiExtendMarkdownChildren markdownFile = new OpenApiExtendMarkdownChildren();
        if (resource != null) {
            this.logger.info(resource.getFilename());
            if (resource.getFilename().toLowerCase().endsWith(".md")) {
                OpenApiExtendMarkdownChildren openApiExtendMarkdownChildren;
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), "UTF-8"));
                    String le = null;
                    String title = resource.getFilename();
                    String reg = "#{1,3}\\s{1}(.*)";
                    Pattern pattern = Pattern.compile(reg, 2);
                    Matcher matcher = null;
                    le = reader.readLine();
                    if (le != null && (matcher = pattern.matcher(le)).matches()) {
                        title = matcher.group(1);
                    }
                    CommonUtils.close((Reader)reader);
                    markdownFile.setTitle(title);
                    markdownFile.setContent(new String(CommonUtils.readBytes((InputStream)resource.getInputStream()), "UTF-8"));
                    openApiExtendMarkdownChildren = markdownFile;
                }
                catch (Exception e) {
                    try {
                        this.logger.warn("(Ignores) Failed to read Markdown files,Error Message:{} ", (Object)e.getMessage());
                    }
                    catch (Throwable throwable) {
                        CommonUtils.close(reader);
                        throw throwable;
                    }
                    CommonUtils.close((Reader)reader);
                }
                CommonUtils.close((Reader)reader);
                return openApiExtendMarkdownChildren;
            }
        }
        return null;
    }
}

