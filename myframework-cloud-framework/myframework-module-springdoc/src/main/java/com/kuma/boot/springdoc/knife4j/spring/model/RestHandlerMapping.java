/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.web.bind.annotation.RequestMethod
 */
package com.kuma.boot.springdoc.knife4j.spring.model;

import java.lang.reflect.Method;
import java.util.Set;
import org.springframework.web.bind.annotation.RequestMethod;

public class RestHandlerMapping {
    private String url;
    private Class<?> beanType;
    private Method beanOfMethod;
    private Set<RequestMethod> requestMethods;

    public Set<RequestMethod> getRequestMethods() {
        return this.requestMethods;
    }

    public void setRequestMethods(Set<RequestMethod> requestMethods) {
        this.requestMethods = requestMethods;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Class<?> getBeanType() {
        return this.beanType;
    }

    public void setBeanType(Class<?> beanType) {
        this.beanType = beanType;
    }

    public Method getBeanOfMethod() {
        return this.beanOfMethod;
    }

    public void setBeanOfMethod(Method beanOfMethod) {
        this.beanOfMethod = beanOfMethod;
    }

    public RestHandlerMapping(String url, Class<?> beanType, Method beanOfMethod, Set<RequestMethod> requestMethods) {
        this.url = url;
        this.beanType = beanType;
        this.beanOfMethod = beanOfMethod;
        this.requestMethods = requestMethods;
    }

    public RestHandlerMapping() {
    }
}

