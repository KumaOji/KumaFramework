/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.web.servlet.config.annotation.PathMatchConfigurer
 */
package com.kuma.boot.web.autoconfigure.customizer;

import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;

public interface KmcWebMvcConfigurer {
    default public void configurePathMatch(PathMatchConfigurer configurer) {
    }
}

