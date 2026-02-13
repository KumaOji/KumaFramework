/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.extension;

import com.kuma.boot.common.extension.active.ActiveExtensionLoader;
import com.kuma.boot.common.extension.adaptive.AdaptiveExtensionLoader;
import com.kuma.boot.common.extension.common.Constants;
import com.kuma.boot.common.extension.context.Lifecycle;
import com.kuma.boot.common.extension.strategy.LoadingStrategy;
import com.kuma.boot.common.extension.strategy.LoadingStrategyHolder;
import com.kuma.boot.common.extension.util.Holder;
import com.kuma.boot.common.extension.wrapper.WrapperComparator;
import com.kuma.boot.common.extension.wrapper.WrapperExtensionLoader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.lang3.ArrayUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 扩展点加载器
 *
 */
public class ExtensionLoader<T> {

    private static final Logger log = LoggerFactory.getLogger(ActiveExtensionLoader.class);

    /**
     * 扩展点加载器（延迟加载）
     */
    private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS =
            new ConcurrentHashMap<>(64);

    /**
     * 扩展实例 扩展点实例（延迟加载）
     */
    private static final ConcurrentMap<Class<?>, Object> EXTENSION_INSTANCES =
            new ConcurrentHashMap<>(64);

    /**
     * 缓存扩展点名称
     */
    private final ConcurrentMap<Class<?>, String> cachedNames = new ConcurrentHashMap<>();

    /**
     * 缓存扩展点Class（重点）
     */
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    /**
     * 缓存扩展点实例
     */
    private final ConcurrentMap<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();

    /**
     * 缓存异常信息
     */
    private final Map<String, IllegalStateException> exceptions = new ConcurrentHashMap<>();

    /**
     * 处理 @Active 扩展点
     */
    private final ActiveExtensionLoader<T> activeExtensionLoader =
            new ActiveExtensionLoader<>(this);

    /**
     * 处理 @Wrapper 扩展点
     */
    private final WrapperExtensionLoader wrapperExtensionLoader = new WrapperExtensionLoader();

    /**
     * 处理 @Adaptive 扩展点
     */
    private final AdaptiveExtensionLoader<T> adaptiveExtensionLoader =
            new AdaptiveExtensionLoader<>(this);

    /**
     * 缓存默认的扩展点名称
     */
    private String cachedDefaultName;

    /**
     * 扩展点类型
     */
    private final Class<?> type;

    /**
     * 扩展点实例工厂
     */
    private final ExtensionFactory objectFactory;

    /**
     * 扩展加载程序
     * @param type 类型
     * @since 2023-07-04 09:22:43
     */
    private ExtensionLoader(Class<?> type) {
        this.type = type;
        this.objectFactory =
                (type == ExtensionFactory.class
                        ? null
                        : ExtensionLoader.getExtensionLoader(ExtensionFactory.class)
                        .getAdaptiveExtension());
    }

    /**
     * 获取扩展点加载器
     * @param type 类型
     * @return {@link ExtensionLoader }<{@link T }>
     * @since 2023-07-04 09:22:47
     */
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        checkExtensionAnnotation(type);
        @SuppressWarnings("unchecked")
        ExtensionLoader<T> loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        if (loader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<T>(type));
            @SuppressWarnings("unchecked")
            ExtensionLoader<T> loader1 = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
            loader = loader1;
        }
        return loader;
    }

    /**
     * 获取 @Adaptive 扩展点
     * @return {@link T }
     * @since 2023-07-04 09:22:50
     */
    public T getAdaptiveExtension() {
        return adaptiveExtensionLoader.getAdaptiveExtension();
    }

    /**
     * 获取支持的扩展点
     * @return {@link Set }<{@link String }>
     * @since 2023-07-04 09:22:53
     */
    public Set<String> getSupportedExtensions() {
        Map<String, Class<?>> classes = getExtensionClasses();
        return Collections.unmodifiableSet(new TreeSet<>(classes.keySet()));
    }

    /**
     * 根据指定的名称获取扩展点
     * @param name name
     * @return {@link T }
     * @since 2023-07-04 09:22:55
     */
    public T getExtension(String name) {
        return getExtension(name, true);
    }

    /**
     * 根据指定的名称获取扩展点
     * @param name name
     * @param wrap wrap
     * @return {@link T }
     * @since 2023-07-04 09:22:58
     */
    public T getExtension(String name, boolean wrap) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Extension name == null");
        }
        final Holder<Object> holder = getOrCreateHolder(name);
        Object instance = holder.get();
        if (instance == null) {
            // 锁住 holder 的对象引用地址
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name, wrap);
                    holder.set(instance);
                }
            }
        }
        @SuppressWarnings("unchecked")
        T t = (T) instance;
        return t;
    }

    public Map<String, T> getExtensionMap() {
        Map<String, Class<?>> extensionClasses = this.getExtensionClasses();
        Map<String, T> result = new ConcurrentHashMap<>();
        for (String name : extensionClasses.keySet()) {
            T extension = getExtension(name);
            result.put(name, extension);
        }
        return result;
    }

    /**
     * 获取或者创建缓存扩展点实例的容器
     * @param name name
     * @return {@link Holder }<{@link Object }>
     * @since 2023-07-04 09:23:01
     */
    private Holder<Object> getOrCreateHolder(String name) {
        // 从缓存根据指定名称获取扩展点实例的持有者
        Holder<Object> holder = cachedInstances.get(name);
        if (holder == null) {
            // 如果持有者为空，则创建一个对象并放入缓存
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        return holder;
    }

    /**
     * 获取扩展点名称
     * @param extensionInstance extensionInstance
     * @return {@link String }
     * @since 2023-07-04 09:23:03
     */
    public String getExtensionName(T extensionInstance) {
        return getExtensionName(extensionInstance.getClass());
    }

    /**
     * 获取扩展点名称
     * @param extensionClass extensionClass
     * @return {@link String }
     * @since 2023-07-04 09:23:06
     */
    public String getExtensionName(Class<?> extensionClass) {
        this.getExtensionClasses();
        return cachedNames.get(extensionClass);
    }

    /**
     * 根据指定的名称获取扩展类
     * @param name name
     * @return {@link Class }<{@link ? }>
     * @since 2023-07-04 09:23:08
     */
    public Class<?> getExtensionClass(String name) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type == null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Extension name == null");
        }
        return getExtensionClasses().get(name);
    }

    /**
     * 根据指定的名称获取已加载的扩展点
     * @param name name
     * @return {@link T }
     * @since 2023-07-04 09:23:11
     */
    @SuppressWarnings("unchecked")
    public T getLoadedExtension(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Extension name == null");
        }
        Holder<Object> holder = getOrCreateHolder(name);
        return (T) holder.get();
    }

    /**
     * 获取已加载的扩展点
     * @return {@link Set }<{@link String }>
     * @since 2023-07-04 09:23:13
     */
    public Set<String> getLoadedExtensions() {
        return Collections.unmodifiableSet(new TreeSet<>(cachedInstances.keySet()));
    }

    /**
     * 获取已加载的扩展点实例
     * @return {@link List }<{@link T }>
     * @since 2023-07-04 09:23:15
     */
    public List<T> getLoadedExtensionInstances() {
        List<T> instances = new ArrayList<>();
        cachedInstances
                .values()
                .forEach(
                        holder -> {
                            @SuppressWarnings("unchecked")
                            T t = (T) holder.get();

                            instances.add(t);
                        });
        return instances;
    }

    /**
     * 获取默认的扩展点
     * @return {@link T }
     * @since 2023-07-04 09:23:17
     */
    public T getDefaultExtension() {
        this.getExtensionClasses();
        if (StringUtils.isBlank(cachedDefaultName)) {
            return null;
        }
        return getExtension(cachedDefaultName);
    }

    /**
     * 根据指定的名称获取扩展点
     * @param name name
     * @return {@link T }
     * @since 2023-07-04 09:23:20
     */
    public T getOrDefaultExtension(String name) {
        return containsExtension(name) ? getExtension(name) : getDefaultExtension();
    }

    /**
     * 创建扩展点实例
     * @param name name
     * @param wrap wrap
     * @return {@link T }
     * @since 2023-07-04 09:23:22
     */
    private T createExtension(String name, boolean wrap) {
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw findException(name);
        }
        try {
            @SuppressWarnings("unchecked")
            T instance = (T) EXTENSION_INSTANCES.get(clazz);
            if (instance == null) {
                EXTENSION_INSTANCES.putIfAbsent(
                        clazz, clazz.getDeclaredConstructor().newInstance());
                @SuppressWarnings("unchecked")
                T instance1 = (T) EXTENSION_INSTANCES.get(clazz);
                instance = instance1;
            }
            injectExtension(instance);

            if (wrap) {
                List<Class<?>> wrapperClassesList = new ArrayList<>();
                if (wrapperExtensionLoader.getCachedWrapperClasses() != null) {
                    wrapperClassesList.addAll(wrapperExtensionLoader.getCachedWrapperClasses());
                    wrapperClassesList.sort(WrapperComparator.COMPARATOR);
                    Collections.reverse(wrapperClassesList);
                }

                if (CollectionUtils.isNotEmpty(wrapperClassesList)) {
                    for (Class<?> wrapperClass : wrapperClassesList) {
                        Wrapper wrapper = wrapperClass.getAnnotation(Wrapper.class);
                        if (wrapper == null
                                || (ArrayUtils.contains(wrapper.matches(), name)
                                && !ArrayUtils.contains(wrapper.mismatches(), name))) {

                            @SuppressWarnings("unchecked")
                            T t = (T) wrapperClass.getConstructor(type).newInstance(instance);
                            instance = injectExtension(t);
                        }
                    }
                }
            }

            initExtension(instance);
            return instance;
        } catch (Throwable t) {
            throw new IllegalStateException(
                    "Extension instance (name: "
                            + name
                            + ", class: "
                            + type
                            + ") couldn't be instantiated: "
                            + t.getMessage(),
                    t);
        }
    }

    /**
     * 判断指定名称的扩展点是否已存在
     * @param name name
     * @return boolean
     * @since 2023-07-04 09:21:15
     */
    private boolean containsExtension(String name) {
        return getExtensionClasses().containsKey(name);
    }

    /**
     * 加载扩展点Classes
     * @return {@link Map }<{@link String }, {@link Class }<{@link ? }>>
     * @since 2023-07-04 09:21:15
     */
    public Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = cachedClasses.get();
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    // 缓存默认的扩展点名称
                    this.cacheDefaultExtensionName();
                    // 遍历 LoadingStrategy，获取扩展点Classes
                    classes = this.loadExtensionClasses();
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    /**
     * 遍历 LoadingStrategy，获取扩展点Classes
     * @return {@link Map }<{@link String }, {@link Class }<{@link ? }>>
     * @since 2023-07-04 09:21:15
     */
    private Map<String, Class<?>> loadExtensionClasses() {
        Map<String, Class<?>> extensionClasses = new HashMap<>();
        for (LoadingStrategy strategy : LoadingStrategyHolder.strategies) {
            loadDirectory(
                    extensionClasses,
                    strategy.directory(),
                    type.getName(),
                    strategy.preferExtensionClassLoader(),
                    strategy.overridden(),
                    strategy.excludedPackages());
        }
        return extensionClasses;
    }

    /**
     * 根据 LoadingStrategy 加载指定的 Directory
     * @param extensionClasses 扩展类
     * @param dir dir
     * @param type 类型
     * @param extensionLoaderClassLoaderFirst 装载机扩展类装入器
     * @param overridden 覆盖
     * @param excludedPackages 排除包
     * @since 2023-07-04 09:23:42
     */
    private void loadDirectory(
            Map<String, Class<?>> extensionClasses,
            String dir,
            String type,
            boolean extensionLoaderClassLoaderFirst,
            boolean overridden,
            String... excludedPackages) {
        String fileName = dir + type;
        try {
            Enumeration<URL> urls = null;
            ClassLoader classLoader = ClassLoaderUtils.getClassLoader(ExtensionLoader.class);
            if (extensionLoaderClassLoaderFirst) {
                ClassLoader extensionLoaderClassLoader = ExtensionLoader.class.getClassLoader();
                if (ClassLoader.getSystemClassLoader() != extensionLoaderClassLoader) {
                    urls = extensionLoaderClassLoader.getResources(fileName);
                }
            }

            if (urls == null || !urls.hasMoreElements()) {
                if (classLoader != null) {
                    urls = classLoader.getResources(fileName);
                } else {
                    urls = ClassLoader.getSystemResources(fileName);
                }
            }

            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    loadResource(
                            extensionClasses,
                            classLoader,
                            resourceUrl,
                            overridden,
                            excludedPackages);
                }
            }
        } catch (Throwable t) {
            log.error(
                    "Exception occurred when loading extension class (interface: "
                            + type
                            + ", description file: "
                            + fileName
                            + ").",
                    t);
        }
    }

    /**
     * 根据 Directory 位置加载指定的 Resource
     * @param extensionClasses 扩展类
     * @param classLoader 类装入器
     * @param resourceURL 资源url
     * @param overridden 覆盖
     * @param excludedPackages 排除包
     * @since 2023-07-04 09:24:11
     */
    private void loadResource(
            Map<String, Class<?>> extensionClasses,
            ClassLoader classLoader,
            URL resourceURL,
            boolean overridden,
            String... excludedPackages) {
        try {
            try (BufferedReader reader =
                         new BufferedReader(
                                 new InputStreamReader(
                                         resourceURL.openStream(), StandardCharsets.UTF_8))) {
                String line;
                String clazz;
                while ((line = reader.readLine()) != null) {
                    final int ci = line.indexOf('#');
                    if (ci >= 0) {
                        line = line.substring(0, ci);
                    }
                    line = line.trim();
                    if (!line.isEmpty()) {
                        try {
                            String name = null;
                            int i = line.indexOf('=');
                            if (i > 0) {
                                name = line.substring(0, i).trim();
                                clazz = line.substring(i + 1).trim();
                            } else {
                                clazz = line;
                            }
                            if (StringUtils.isNotEmpty(clazz)
                                    && !isExcluded(clazz, excludedPackages)) {
                                loadClass(
                                        extensionClasses,
                                        resourceURL,
                                        Class.forName(clazz, true, classLoader),
                                        name,
                                        overridden);
                            }
                        } catch (Throwable t) {
                            IllegalStateException e =
                                    new IllegalStateException(
                                            "Failed to load extension class (interface: "
                                                    + type
                                                    + ", class line: "
                                                    + line
                                                    + ") in "
                                                    + resourceURL
                                                    + ", cause: "
                                                    + t.getMessage(),
                                            t);
                            exceptions.put(line, e);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            log.error(
                    "Exception occurred when loading extension class (interface: "
                            + type
                            + ", class file: "
                            + resourceURL
                            + ") in "
                            + resourceURL,
                    t);
        }
    }

    /**
     * 根据 Resource 加载指定的 Classes
     * @param extensionClasses 扩展类
     * @param resourceURL 资源url
     * @param clazz clazz
     * @param name 名字
     * @param overridden 覆盖
     * @since 2023-07-04 09:24:21
     */
    private void loadClass(
            Map<String, Class<?>> extensionClasses,
            URL resourceURL,
            Class<?> clazz,
            String name,
            boolean overridden)
            throws NoSuchMethodException {
        if (!type.isAssignableFrom(clazz)) {
            throw new IllegalStateException(
                    "Error occurred when loading extension class (interface: "
                            + type
                            + ", class line: "
                            + clazz.getName()
                            + "), class "
                            + clazz.getName()
                            + " is not subtype of interface.");
        }

        // 处理 @Adaptive 注解
        if (isAdaptiveClass(clazz)) {
            adaptiveExtensionLoader.cacheAdaptiveClass(clazz, overridden);
            return;
        }

        // 处理 @Wrapper 注解
        if (isWrapperClass(clazz)) {
            wrapperExtensionLoader.cacheWrapperClass(clazz);
            return;
        }

        // 检测 clazz 是否有默认的构造方法，如果没有，则抛出异常
        clazz.getConstructor();

        // 如果名称为空，从类名匹配
        if (StringUtils.isEmpty(name)) {
            name = findExtensionName(clazz);
            if (name.isEmpty()) {
                throw new IllegalStateException(
                        "No such extension name for the class "
                                + clazz.getName()
                                + " in the config "
                                + resourceURL);
            }
        }

        String[] names = Constants.COMMA_SPLIT_PATTERN.split(name);
        if (ArrayUtils.isNotEmpty(names)) {
            activeExtensionLoader.cacheActivateClass(clazz, names[0]);
            for (String n : names) {
                saveInCacheName(clazz, n);
                saveInExtensionClass(extensionClasses, clazz, n, overridden);
            }
        }
    }

    /**
     * 缓存默认的扩展点名称
     *
     * @since 2023-07-04 09:24:26
     */
    private void cacheDefaultExtensionName() {
        final SPI defaultAnnotation = type.getAnnotation(SPI.class);
        if (defaultAnnotation == null) {
            return;
        }

        String value = defaultAnnotation.value().trim();
        if (!value.isEmpty()) {
            String[] names = Constants.COMMA_SPLIT_PATTERN.split(value);
            if (names.length > 1) {
                throw new IllegalStateException(
                        "More than 1 default extension name on extension "
                                + type.getName()
                                + ": "
                                + Arrays.toString(names));
            }
            if (names.length == 1) {
                this.cachedDefaultName = names[0];
            }
        }
    }

    /**
     * 是否标注 @Adapter
     * @param clazz clazz
     * @return boolean
     * @since 2023-07-04 09:24:29
     */
    private boolean isAdaptiveClass(Class<?> clazz) {
        return clazz.isAnnotationPresent(Adaptive.class);
    }

    /**
     * 是否标注 @Wrapper
     * @param clazz clazz
     * @return boolean
     * @since 2023-07-04 09:24:31
     */
    private boolean isWrapperClass(Class<?> clazz) {
        try {
            clazz.getConstructor(type);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * 查找扩展点名称
     * @param clazz clazz
     * @return {@link String }
     * @since 2023-07-04 09:24:34
     */
    private String findExtensionName(Class<?> clazz) {
        String name = clazz.getSimpleName();
        if (name.endsWith(type.getSimpleName())) {
            name = name.substring(0, name.length() - type.getSimpleName().length());
        }
        return name.toLowerCase();
    }

    /**
     * 保存缓存的扩展类名称
     * @param clazz clazz
     * @param name 名字
     * @since 2023-07-04 09:24:40
     */
    private void saveInCacheName(Class<?> clazz, String name) {
        if (!cachedNames.containsKey(clazz)) {
            cachedNames.put(clazz, name);
        }
    }

    /**
     * 保存扩展类
     * @param extensionClasses 扩展类
     * @param clazz clazz
     * @param name 名字
     * @param overridden 覆盖
     * @since 2023-07-04 09:24:49
     */
    private void saveInExtensionClass(
            Map<String, Class<?>> extensionClasses,
            Class<?> clazz,
            String name,
            boolean overridden) {
        Class<?> c = extensionClasses.get(name);
        if (c == null || overridden) {
            extensionClasses.put(name, clazz);
        } else if (c != clazz) {
            // 新增顺序替换
            if (clazz.isAnnotationPresent(Order.class) || c.isAnnotationPresent(Order.class)) {
                Order destOrder = clazz.getAnnotation(Order.class);
                int destValue = destOrder != null ? destOrder.value() : 0;
                Order srcOrder = c.getAnnotation(Order.class);
                int srcValue = srcOrder != null ? srcOrder.value() : 0;
                if (srcValue > destValue) {
                    log.debug(
                            "Compare extension "
                                    + type.getName()
                                    + " name "
                                    + name
                                    + " use "
                                    + clazz.getName()
                                    + " instead of "
                                    + c.getName());
                    extensionClasses.put(name, clazz);
                }
                log.debug(
                        "Compare extension "
                                + type.getName()
                                + " name "
                                + name
                                + " use "
                                + c.getName()
                                + " ignore "
                                + clazz.getName());
                return;
            }

            String duplicateMsg =
                    "Duplicate extension "
                            + type.getName()
                            + " name "
                            + name
                            + " on "
                            + c.getName()
                            + " and "
                            + clazz.getName();
            log.error(duplicateMsg);
            throw new IllegalStateException(duplicateMsg);
        }
    }

    /**
     * 是否排除类
     * @param className className
     * @param excludedPackages excludedPackages
     * @return boolean
     * @since 2023-07-04 09:24:55
     */
    private boolean isExcluded(String className, String... excludedPackages) {
        if (excludedPackages != null) {
            for (String excludePackage : excludedPackages) {
                if (className.startsWith(excludePackage + ".")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检查扩展点规范
     * @param type type
     * @since 2023-07-04 09:24:58
     */
    private static <T> void checkExtensionAnnotation(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type == null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException(
                    "Extension type (" + type + ") is not an interface!");
        }
        if (!type.isAnnotationPresent(SPI.class)) {
            throw new IllegalArgumentException(
                    "Extension type ("
                            + type
                            + ") is not an extension, because it is NOT annotated with @"
                            + SPI.class.getSimpleName()
                            + "!");
        }
    }

    /**
     * 根据指定的名称查找异常
     * @param name name
     * @return {@link IllegalStateException }
     * @since 2023-07-04 09:25:00
     */
    private IllegalStateException findException(String name) {
        StringBuilder buf =
                new StringBuilder("No such extension " + type.getName() + " by name " + name);

        int i = 1;
        for (Map.Entry<String, IllegalStateException> entry : exceptions.entrySet()) {
            if (entry.getKey().toLowerCase().startsWith(name.toLowerCase())) {
                if (i == 1) {
                    buf.append(", possible causes: ");
                }
                buf.append("\r\n(");
                buf.append(i++);
                buf.append(") ");
                buf.append(entry.getKey());
                buf.append(":\r\n");
                buf.append(entry.getValue());
            }
        }

        if (i == 1) {
            buf.append(
                    ", no related exception was found, please check whether related SPI module is"
                            + " missing.");
        }
        return new IllegalStateException(buf.toString());
    }

    /**
     * 注入扩展点
     * @param instance instance
     * @return {@link T }
     * @since 2023-07-04 09:25:03
     */
    public T injectExtension(T instance) {
        if (objectFactory == null) {
            return instance;
        }

        try {
            for (Method method : instance.getClass().getMethods()) {
                if (!ReflectionUtils.isSetter(method)) {
                    continue;
                }

                Class<?> pt = method.getParameterTypes()[0];
                if (ReflectionUtils.isPrimitives(pt)) {
                    continue;
                }

                String property = ReflectionUtils.getSetterProperty(method);
                Inject inject = method.getAnnotation(Inject.class);
                if (inject == null) {
                    injectValue(instance, method, pt, property);
                } else {
                    if (!inject.enable()) {
                        continue;
                    }

                    if (inject.type() == Inject.InjectType.ByType) {
                        injectValue(instance, method, pt, null);
                    } else {
                        injectValue(instance, method, pt, property);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return instance;
    }

    /**
     * 注入属性
     * @param instance 实例
     * @param method 方法
     * @param pt pt
     * @param property 财产
     * @since 2023-07-04 09:25:10
     */
    private void injectValue(T instance, Method method, Class<?> pt, String property) {
        try {
            Object object = objectFactory.getExtension(pt, property);
            if (object != null) {
                method.invoke(instance, object);
            }
        } catch (Exception e) {
            log.error(
                    "Failed to inject via method "
                            + method.getName()
                            + " of interface "
                            + type.getName()
                            + ": "
                            + e.getMessage(),
                    e);
        }
    }

    /**
     * TODO 初始化扩展点
     * @param instance 实例
     * @since 2023-07-04 09:25:24
     */
    private void initExtension(T instance) {
        if (instance instanceof Lifecycle lifecycle) {
            lifecycle.initialize();
        }
    }

    public ConcurrentMap<Class<?>, String> getCachedNames() {
        return cachedNames;
    }

    public Holder<Map<String, Class<?>>> getCachedClasses() {
        return cachedClasses;
    }

    public ConcurrentMap<String, Holder<Object>> getCachedInstances() {
        return cachedInstances;
    }

    public Map<String, IllegalStateException> getExceptions() {
        return exceptions;
    }

    public ActiveExtensionLoader<T> getActiveExtensionLoader() {
        return activeExtensionLoader;
    }

    public WrapperExtensionLoader getWrapperExtensionLoader() {
        return wrapperExtensionLoader;
    }

    public AdaptiveExtensionLoader<T> getAdaptiveExtensionLoader() {
        return adaptiveExtensionLoader;
    }

    public String getCachedDefaultName() {
        return cachedDefaultName;
    }

    public void setCachedDefaultName(String cachedDefaultName) {
        this.cachedDefaultName = cachedDefaultName;
    }

    public Class<?> getType() {
        return type;
    }

    public ExtensionFactory getObjectFactory() {
        return objectFactory;
    }
}
