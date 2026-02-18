/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.extension.adaptive;

import com.kuma.boot.common.extension.ClassLoaderUtils;
import com.kuma.boot.common.extension.ExtensionLoader;
import com.kuma.boot.common.extension.compile.Compiler;
import com.kuma.boot.common.extension.util.Holder;

/**
 * 基于 @Adaptive 的扩展点加载器
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-10 16:37:20
 */
public class AdaptiveExtensionLoader<T> {

    /**
     * 缓存自适应扩展点实例
     */
    private final Holder<Object> cachedAdaptiveInstance = new Holder<>();

    /**
     * 自适应缓存类
     */
    private volatile Class<?> cachedAdaptiveClass = null;

    /**
     * 创建自适应错误实例
     */
    private volatile Throwable createAdaptiveInstanceError;

    /**
     * 扩展加载程序
     */
    private final ExtensionLoader<T> extensionLoader;

    /**
     * 自适应扩展加载程序
     * @param extensionLoader 扩展加载程序
     * @since 2023-07-10 16:37:30
     */
    public AdaptiveExtensionLoader(ExtensionLoader<T> extensionLoader) {
        this.extensionLoader = extensionLoader;
    }

    /**
     * 获取 @Adaptive 扩展点
     * @return {@link T }
     * @since 2023-07-10 16:37:20
     */
    public T getAdaptiveExtension() {
        Object instance = cachedAdaptiveInstance.get();
        if (instance == null) {
            if (createAdaptiveInstanceError != null) {
                throw new IllegalStateException(
                        "Failed to create adaptive instance: "
                                + createAdaptiveInstanceError.toString(),
                        createAdaptiveInstanceError);
            }

            synchronized (cachedAdaptiveInstance) {
                instance = cachedAdaptiveInstance.get();
                if (instance == null) {
                    try {
                        instance = createAdaptiveExtension();
                        cachedAdaptiveInstance.set(instance);
                    } catch (Throwable t) {
                        createAdaptiveInstanceError = t;
                        throw new IllegalStateException(
                                "Failed to create adaptive instance: " + t.toString(), t);
                    }
                }
            }
        }
        @SuppressWarnings("unchecked")
        T t = (T) instance;
        return t;
    }

    /**
     * 缓存 @Adaptive 扩展类
     * @param clazz clazz
     * @param overridden overridden
     */
    public void cacheAdaptiveClass(Class<?> clazz, boolean overridden) {
        if (cachedAdaptiveClass == null || overridden) {
            cachedAdaptiveClass = clazz;
        } else if (!cachedAdaptiveClass.equals(clazz)) {
            throw new IllegalStateException(
                    "More than 1 adaptive class found: "
                            + cachedAdaptiveClass.getName()
                            + ", "
                            + clazz.getName());
        }
    }

    public Object getLoadedAdaptiveExtensionInstances() {
        return cachedAdaptiveInstance.get();
    }

    /**
     * 创建 @Adaptive 扩展点
     * @return T
     */
    private T createAdaptiveExtension() {
        try {
            @SuppressWarnings("unchecked")
            T t = (T) getAdaptiveExtensionClass().getDeclaredConstructor().newInstance();
            return extensionLoader.injectExtension(t);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Can't create adaptive extension "
                            + extensionLoader.getType()
                            + ", cause: "
                            + e.getMessage(),
                    e);
        }
    }

    /**
     * 获取 @Adaptive 扩展类
     * @return Class<?>
     */
    private Class<?> getAdaptiveExtensionClass() {
        extensionLoader.getExtensionClasses();
        if (cachedAdaptiveClass != null) {
            return cachedAdaptiveClass;
        }
        return cachedAdaptiveClass = createAdaptiveExtensionClass();
    }

    /**
     * 创建 @Adaptive 扩展类
     * @return Class<?>
     */
    private Class<?> createAdaptiveExtensionClass() {
        String code =
                new AdaptiveClassCodeGenerator(
                        extensionLoader.getType(), extensionLoader.getCachedDefaultName())
                        .generate();
        ClassLoader classLoader = ClassLoaderUtils.getClassLoader(ExtensionLoader.class);
        Compiler compiler =
                ExtensionLoader.getExtensionLoader(Compiler.class).getAdaptiveExtension();
        return compiler.compile(code, classLoader);
    }
}
