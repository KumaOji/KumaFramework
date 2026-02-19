/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.github.xiaoymin.knife4j.core.extend.OpenApiExtendMarkdownChildren
 */
package com.kuma.boot.springdoc.knife4j.spring.model;

import com.github.xiaoymin.knife4j.core.extend.OpenApiExtendMarkdownChildren;

import java.util.ArrayList;
import java.util.List;

public class SwaggerBootstrapUi {
    protected List<SwaggerBootstrapUiTag> tagSortLists = new ArrayList<SwaggerBootstrapUiTag>();
    private List<SwaggerBootstrapUiPath> pathSortLists = new ArrayList<SwaggerBootstrapUiPath>();
    private List<OpenApiExtendMarkdownChildren> markdownFiles = new ArrayList<OpenApiExtendMarkdownChildren>();
    private String errorMsg;

    public List<SwaggerBootstrapUiPath> getPathSortLists() {
        return this.pathSortLists;
    }

    public void setPathSortLists(List<SwaggerBootstrapUiPath> pathSortLists) {
        this.pathSortLists = pathSortLists;
    }

    public List<SwaggerBootstrapUiTag> getTagSortLists() {
        return this.tagSortLists;
    }

    public void setTagSortLists(List<SwaggerBootstrapUiTag> tagSortLists) {
        this.tagSortLists = tagSortLists;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<OpenApiExtendMarkdownChildren> getMarkdownFiles() {
        return this.markdownFiles;
    }

    public void setMarkdownFiles(List<OpenApiExtendMarkdownChildren> markdownFiles) {
        this.markdownFiles = markdownFiles;
    }
}

