/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javassist.ClassPath
 *  javassist.ClassPool
 *  javassist.LoaderClassPath
 */
package com.kuma.boot.common.utils.reflect;

import javassist.ClassPath;
import javassist.ClassPool;
import javassist.LoaderClassPath;

public class ClassPoolUtils {
    public static volatile ClassPool instance;

    private ClassPoolUtils() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static ClassPool getInstance() {
        if (instance != null) return instance;
        Class<ClassPoolUtils> clazz = ClassPoolUtils.class;
        synchronized (ClassPoolUtils.class) {
            if (instance != null) return instance;
            ClassPool aDefault = ClassPool.getDefault();
            aDefault.appendClassPath((ClassPath)new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
            instance = aDefault;
            // ** MonitorExit[var0] (shouldn't be in output)
            return instance;
        }
    }
}

