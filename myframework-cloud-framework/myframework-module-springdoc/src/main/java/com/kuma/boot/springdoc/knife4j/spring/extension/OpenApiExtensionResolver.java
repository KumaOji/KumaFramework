/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.github.xiaoymin.knife4j.core.extend.OpenApiExtendMarkdownChildren
 *  com.github.xiaoymin.knife4j.core.extend.OpenApiExtendMarkdownFile
 *  com.github.xiaoymin.knife4j.core.model.MarkdownProperty
 *  com.github.xiaoymin.knife4j.core.util.CollectionUtils
 *  com.github.xiaoymin.knife4j.core.util.CommonUtils
 *  com.github.xiaoymin.knife4j.core.util.StrUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.support.PathMatchingResourcePatternResolver
 *  org.springframework.core.io.support.ResourcePatternResolver
 */
package com.kuma.boot.springdoc.knife4j.spring.extension;

import com.github.xiaoymin.knife4j.core.extend.OpenApiExtendMarkdownChildren;
import com.github.xiaoymin.knife4j.core.extend.OpenApiExtendMarkdownFile;
import com.github.xiaoymin.knife4j.core.model.MarkdownProperty;
import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import com.github.xiaoymin.knife4j.core.util.CommonUtils;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.kuma.boot.springdoc.knife4j.spring.configuration.Knife4jSetting;
import com.kuma.boot.springdoc.knife4j.spring.util.MarkdownUtils;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class OpenApiExtensionResolver {
    Logger logger = LoggerFactory.getLogger(OpenApiExtensionResolver.class);
    private final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
    private final Map<String, List<OpenApiExtendMarkdownFile>> markdownFileMaps = new HashMap<String, List<OpenApiExtendMarkdownFile>>();
    private final Knife4jSetting setting;
    private final List<MarkdownProperty> markdownProperties;

    public List<OpenApiExtendMarkdownFile> getMarkdownFiles() {
        if (CollectionUtils.isNotEmpty(this.markdownFileMaps)) {
            LinkedList<OpenApiExtendMarkdownFile> markdownFiles = new LinkedList<OpenApiExtendMarkdownFile>();
            for (Map.Entry<String, List<OpenApiExtendMarkdownFile>> entry : this.markdownFileMaps.entrySet()) {
                if (!CollectionUtils.isNotEmpty((Collection)entry.getValue())) continue;
                markdownFiles.addAll((Collection<OpenApiExtendMarkdownFile>)entry.getValue());
            }
            return markdownFiles;
        }
        return Collections.EMPTY_LIST;
    }

    public void start() {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Resolver method start...");
        }
        if (CollectionUtils.isNotEmpty(this.markdownProperties)) {
            for (MarkdownProperty markdownProperty : this.markdownProperties) {
                if (!StrUtil.isNotBlank((CharSequence)markdownProperty.getName()) || !StrUtil.isNotBlank((CharSequence)markdownProperty.getLocations())) continue;
                String swaggerGroupName = StrUtil.isNotBlank((CharSequence)markdownProperty.getGroup()) ? markdownProperty.getGroup() : "default";
                OpenApiExtendMarkdownFile openApiExtendMarkdownFile = new OpenApiExtendMarkdownFile();
                openApiExtendMarkdownFile.setName(markdownProperty.getName());
                openApiExtendMarkdownFile.setGroup(swaggerGroupName);
                ArrayList<OpenApiExtendMarkdownChildren> allChildrenLists = new ArrayList<OpenApiExtendMarkdownChildren>();
                Object[] locations = markdownProperty.getLocations().split(";");
                if (!CollectionUtils.isEmpty((Object[])locations)) {
                    for (Object location : locations) {
                        List<OpenApiExtendMarkdownChildren> childrenList;
                        if (!StrUtil.isNotBlank((CharSequence)location) || !CollectionUtils.isNotEmpty(childrenList = this.readLocations((String)location))) continue;
                        allChildrenLists.addAll(childrenList);
                    }
                }
                if (CollectionUtils.isNotEmpty(allChildrenLists)) {
                    openApiExtendMarkdownFile.setChildren(allChildrenLists);
                }
                if (this.markdownFileMaps.containsKey(swaggerGroupName)) {
                    this.markdownFileMaps.get(swaggerGroupName).add(openApiExtendMarkdownFile);
                    continue;
                }
                this.markdownFileMaps.put(swaggerGroupName, CollectionUtils.newArrayList((Object[])new OpenApiExtendMarkdownFile[]{openApiExtendMarkdownFile}));
            }
        }
        if (this.setting != null && this.setting.isEnableHomeCustom() && StrUtil.isNotBlank((CharSequence)this.setting.getHomeCustomPath())) {
            String content = this.readCustomHome(this.setting.getHomeCustomPath());
            this.setting.setHomeCustomLocation(content);
        }
    }

    private String readCustomHome(String customHomeLocation) {
        String customHomeContent = "";
        try {
            Resource[] resources = this.resourceResolver.getResources(customHomeLocation);
            if (resources != null && resources.length > 0) {
                Resource resource = resources[0];
                customHomeContent = new String(CommonUtils.readBytes((InputStream)resource.getInputStream()), "UTF-8");
            }
        }
        catch (Exception e) {
            this.logger.warn("(Ignores) Failed to read CustomeHomeLocation Markdown files,Error Message:{} ", (Object)e.getMessage());
        }
        return customHomeContent;
    }

    private List<OpenApiExtendMarkdownChildren> readLocations(String locations) {
        try {
            ArrayList<OpenApiExtendMarkdownChildren> openApiExtendMarkdownChildrenList = new ArrayList<OpenApiExtendMarkdownChildren>();
            Resource[] resources = this.resourceResolver.getResources(locations);
            if (resources != null && resources.length > 0) {
                for (Resource resource : resources) {
                    OpenApiExtendMarkdownChildren markdownFile = this.readMarkdownChildren(resource);
                    if (markdownFile == null) continue;
                    openApiExtendMarkdownChildrenList.add(markdownFile);
                }
                return openApiExtendMarkdownChildrenList;
            }
        }
        catch (Exception e) {
            this.logger.warn("(Ignores) Failed to read Markdown files,Error Message:{} ", (Object)e.getMessage());
        }
        return null;
    }

    private OpenApiExtendMarkdownChildren readMarkdownChildren(Resource resource) {
        return MarkdownUtils.resolveMarkdownResource(resource);
    }

    public OpenApiExtensionResolver(Knife4jSetting setting, List<MarkdownProperty> markdownProperties) {
        this.setting = setting;
        this.markdownProperties = markdownProperties;
    }
}

