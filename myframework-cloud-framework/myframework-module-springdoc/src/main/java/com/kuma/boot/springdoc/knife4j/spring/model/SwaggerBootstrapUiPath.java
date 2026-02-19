/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.springdoc.knife4j.spring.model;

public class SwaggerBootstrapUiPath {
    private String path;
    private String method;
    private Integer order;

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getOrder() {
        return this.order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public SwaggerBootstrapUiPath(String path, String method, Integer order) {
        this.path = path;
        this.method = method;
        this.order = order;
    }

    public SwaggerBootstrapUiPath() {
    }
}

