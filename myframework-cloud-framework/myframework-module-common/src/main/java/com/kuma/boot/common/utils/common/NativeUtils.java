/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.ClassUtils
 */
package com.kuma.boot.common.utils.common;

import org.springframework.util.ClassUtils;

public final class NativeUtils {
    public static final String GENERATED_CLASS = "org.springframework.aot.StaticSpringFactories";
    public static final boolean GENERATED_CLASS_PRESENT = ClassUtils.isPresent((String)"org.springframework.aot.StaticSpringFactories", null);
    public static final String PROPERTY_IMAGE_CODE_KEY = "org.graalvm.nativeimage.imagecode";
    private static final boolean IS_IMAGE_CODE = System.getProperty("org.graalvm.nativeimage.imagecode") != null;

    public static boolean inNativeImage() {
        return IS_IMAGE_CODE || GENERATED_CLASS_PRESENT;
    }
}

