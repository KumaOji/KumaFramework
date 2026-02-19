/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.github.xiaoymin.knife4j.core.extend.OpenApiExtendMarkdownChildren
 *  com.github.xiaoymin.knife4j.core.util.CommonUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.io.Resource
 */
package com.kuma.boot.springdoc.knife4j.spring.util;

import com.github.xiaoymin.knife4j.core.extend.OpenApiExtendMarkdownChildren;
import com.github.xiaoymin.knife4j.core.util.CommonUtils;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

public class MarkdownUtils {
    static final Logger logger = LoggerFactory.getLogger(MarkdownUtils.class);

    public static OpenApiExtendMarkdownChildren resolveMarkdownResource(Resource resource) {
        try {
            if (resource != null) {
                OpenApiExtendMarkdownChildren markdownFile = new OpenApiExtendMarkdownChildren();
                if (logger.isDebugEnabled()) {
                    logger.debug("read file:" + resource.getFilename());
                }
                if (Objects.toString(resource.getFilename(), "").toLowerCase().endsWith(".md")) {
                    try {
                        String title = CommonUtils.resolveMarkdownTitle((InputStream)resource.getInputStream(), (String)resource.getFilename());
                        markdownFile.setTitle(title);
                        markdownFile.setContent(new String(CommonUtils.readBytes((InputStream)resource.getInputStream()), StandardCharsets.UTF_8));
                        return markdownFile;
                    }
                    catch (Exception e) {
                        logger.warn("(Ignores) Failed to read Markdown files,Error Message:{} ", (Object)e.getMessage());
                    }
                }
            }
        }
        catch (Exception e) {
            logger.warn("(Ignores) Failed to read Markdown files,Error Message:{} ", (Object)e.getMessage());
        }
        return null;
    }
}

