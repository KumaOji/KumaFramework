/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.github.xiaoymin.knife4j.core.extend.OpenApiExtendMarkdownChildren
 */
package com.kuma.boot.springdoc.knife4j.spring.model;

import com.github.xiaoymin.knife4j.core.extend.OpenApiExtendMarkdownChildren;
import java.util.List;

public class MarkdownFolder {
    private String directory;
    private String locations;
    private List<OpenApiExtendMarkdownChildren> markdownFiles;

    public String getDirectory() {
        return this.directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getLocations() {
        return this.locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    public List<OpenApiExtendMarkdownChildren> getMarkdownFiles() {
        return this.markdownFiles;
    }

    public void setMarkdownFiles(List<OpenApiExtendMarkdownChildren> markdownFiles) {
        this.markdownFiles = markdownFiles;
    }
}

