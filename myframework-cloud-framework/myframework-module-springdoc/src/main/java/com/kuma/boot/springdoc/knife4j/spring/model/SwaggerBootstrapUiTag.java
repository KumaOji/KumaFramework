/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  io.swagger.v3.oas.models.tags.Tag
 */
package com.kuma.boot.springdoc.knife4j.spring.model;

import io.swagger.v3.oas.models.tags.Tag;

public class SwaggerBootstrapUiTag
extends Tag {
    private String author;
    private Integer order;

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getOrder() {
        return this.order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public SwaggerBootstrapUiTag() {
    }

    public SwaggerBootstrapUiTag(Integer order) {
        this.order = order;
    }
}

