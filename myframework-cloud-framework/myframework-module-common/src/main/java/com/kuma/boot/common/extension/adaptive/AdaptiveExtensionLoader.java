/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.extension.adaptive;

import com.kuma.boot.common.extension.ClassLoaderUtils;
import com.kuma.boot.common.extension.ExtensionLoader;
import com.kuma.boot.common.extension.adaptive.AdaptiveClassCodeGenerator;
import com.kuma.boot.common.extension.compile.Compiler;
import com.kuma.boot.common.extension.util.Holder;

public class AdaptiveExtensionLoader<T> {
    private final Holder<Object> cachedAdaptiveInstance = new Holder();
    private volatile Class<?> cachedAdaptiveClass = null;
    private volatile Throwable createAdaptiveInstanceError;
    private final ExtensionLoader<T> extensionLoader;

    public AdaptiveExtensionLoader(ExtensionLoader<T> extensionLoader) {
        this.extensionLoader = extensionLoader;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T getAdaptiveExtension() {
        Object instance = this.cachedAdaptiveInstance.get();
        if (instance == null) {
            if (this.createAdaptiveInstanceError != null) {
                throw new IllegalStateException("Failed to create adaptive instance: " + this.createAdaptiveInstanceError.toString(), this.createAdaptiveInstanceError);
            }
            Holder<Object> holder = this.cachedAdaptiveInstance;
            synchronized (holder) {
                instance = this.cachedAdaptiveInstance.get();
                if (instance == null) {
                    try {
                        instance = this.createAdaptiveExtension();
                        this.cachedAdaptiveInstance.set(instance);
                    }
                    catch (Throwable t) {
                        this.createAdaptiveInstanceError = t;
                        throw new IllegalStateException("Failed to create adaptive instance: " + t.toString(), t);
                    }
                }
            }
        }
        Object t = instance;
        return (T)t;
    }

    public void cacheAdaptiveClass(Class<?> clazz, boolean overridden) {
        if (this.cachedAdaptiveClass == null || overridden) {
            this.cachedAdaptiveClass = clazz;
        } else if (!this.cachedAdaptiveClass.equals(clazz)) {
            throw new IllegalStateException("More than 1 adaptive class found: " + this.cachedAdaptiveClass.getName() + ", " + clazz.getName());
        }
    }

    public Object getLoadedAdaptiveExtensionInstances() {
        return this.cachedAdaptiveInstance.get();
    }

    private T createAdaptiveExtension() {
        try {
            Object t = this.getAdaptiveExtensionClass().getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            return this.extensionLoader.injectExtension(t);
        }
        catch (Exception e) {
            throw new IllegalStateException("Can't create adaptive extension " + String.valueOf(this.extensionLoader.getType()) + ", cause: " + e.getMessage(), e);
        }
    }

    private Class<?> getAdaptiveExtensionClass() {
        this.extensionLoader.getExtensionClasses();
        if (this.cachedAdaptiveClass != null) {
            return this.cachedAdaptiveClass;
        }
        this.cachedAdaptiveClass = this.createAdaptiveExtensionClass();
        return this.cachedAdaptiveClass;
    }

    private Class<?> createAdaptiveExtensionClass() {
        String code = new AdaptiveClassCodeGenerator(this.extensionLoader.getType(), this.extensionLoader.getCachedDefaultName()).generate();
        ClassLoader classLoader = ClassLoaderUtils.getClassLoader(ExtensionLoader.class);
        Compiler compiler = ExtensionLoader.getExtensionLoader(Compiler.class).getAdaptiveExtension();
        return compiler.compile(code, classLoader);
    }
}

