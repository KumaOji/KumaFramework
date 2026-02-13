/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.kuma.boot.common.extension;

import com.kuma.boot.common.extension.Adaptive;
import com.kuma.boot.common.extension.ClassLoaderUtils;
import com.kuma.boot.common.extension.CollectionUtils;
import com.kuma.boot.common.extension.ExtensionFactory;
import com.kuma.boot.common.extension.Inject;
import com.kuma.boot.common.extension.Order;
import com.kuma.boot.common.extension.ReflectionUtils;
import com.kuma.boot.common.extension.SPI;
import com.kuma.boot.common.extension.Wrapper;
import com.kuma.boot.common.extension.active.ActiveExtensionLoader;
import com.kuma.boot.common.extension.adaptive.AdaptiveExtensionLoader;
import com.kuma.boot.common.extension.common.Constants;
import com.kuma.boot.common.extension.context.Lifecycle;
import com.kuma.boot.common.extension.strategy.LoadingStrategy;
import com.kuma.boot.common.extension.strategy.LoadingStrategyHolder;
import com.kuma.boot.common.extension.util.Holder;
import com.kuma.boot.common.extension.wrapper.WrapperComparator;
import com.kuma.boot.common.extension.wrapper.WrapperExtensionLoader;
import com.kuma.boot.common.utils.lang.StringUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionLoader<T> {
    private static final Logger log = LoggerFactory.getLogger(ActiveExtensionLoader.class);
    private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap(64);
    private static final ConcurrentMap<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap(64);
    private final ConcurrentMap<Class<?>, String> cachedNames = new ConcurrentHashMap();
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder();
    private final ConcurrentMap<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<String, Holder<Object>>();
    private final Map<String, IllegalStateException> exceptions = new ConcurrentHashMap<String, IllegalStateException>();
    private final ActiveExtensionLoader<T> activeExtensionLoader = new ActiveExtensionLoader(this);
    private final WrapperExtensionLoader wrapperExtensionLoader = new WrapperExtensionLoader();
    private final AdaptiveExtensionLoader<T> adaptiveExtensionLoader = new AdaptiveExtensionLoader(this);
    private String cachedDefaultName;
    private final Class<?> type;
    private final ExtensionFactory objectFactory;

    private ExtensionLoader(Class<?> type) {
        this.type = type;
        this.objectFactory = type == ExtensionFactory.class ? null : ExtensionLoader.getExtensionLoader(ExtensionFactory.class).getAdaptiveExtension();
    }

    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        checkExtensionAnnotation(type);
        ExtensionLoader<?> loader = EXTENSION_LOADERS.get(type);
        if (loader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<>(type));
            loader = EXTENSION_LOADERS.get(type);
        }
        return (ExtensionLoader<T>) loader;
    }

    public T getAdaptiveExtension() {
        return this.adaptiveExtensionLoader.getAdaptiveExtension();
    }

    public Set<String> getSupportedExtensions() {
        Map<String, Class<?>> classes = this.getExtensionClasses();
        return Collections.unmodifiableSet(new TreeSet<String>(classes.keySet()));
    }

    public T getExtension(String name) {
        return this.getExtension(name, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T getExtension(String name, boolean wrap) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Extension name == null");
        }
        Holder<Object> holder = this.getOrCreateHolder(name);
        Object instance = holder.get();
        if (instance == null) {
            Holder<Object> holder2 = holder;
            synchronized (holder2) {
                instance = holder.get();
                if (instance == null) {
                    instance = this.createExtension(name, wrap);
                    holder.set(instance);
                }
            }
        }
        Object t = instance;
        return (T)t;
    }

    public Map<String, T> getExtensionMap() {
        Map<String, Class<?>> extensionClasses = this.getExtensionClasses();
        ConcurrentHashMap<String, T> result = new ConcurrentHashMap<String, T>();
        for (String name : extensionClasses.keySet()) {
            T extension = this.getExtension(name);
            result.put(name, extension);
        }
        return result;
    }

    private Holder<Object> getOrCreateHolder(String name) {
        Holder holder = (Holder)this.cachedInstances.get(name);
        if (holder == null) {
            this.cachedInstances.putIfAbsent(name, new Holder());
            holder = (Holder)this.cachedInstances.get(name);
        }
        return holder;
    }

    public String getExtensionName(T extensionInstance) {
        return this.getExtensionName(extensionInstance.getClass());
    }

    public String getExtensionName(Class<?> extensionClass) {
        this.getExtensionClasses();
        return (String)this.cachedNames.get(extensionClass);
    }

    public Class<?> getExtensionClass(String name) {
        if (this.type == null) {
            throw new IllegalArgumentException("Extension type == null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Extension name == null");
        }
        return this.getExtensionClasses().get(name);
    }

    public T getLoadedExtension(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Extension name == null");
        }
        Holder<Object> holder = this.getOrCreateHolder(name);
        return (T)holder.get();
    }

    public Set<String> getLoadedExtensions() {
        return Collections.unmodifiableSet(new TreeSet(this.cachedInstances.keySet()));
    }

    public List<T> getLoadedExtensionInstances() {
        ArrayList instances = new ArrayList();
        this.cachedInstances.values().forEach(holder -> {
            Object t = holder.get();
            instances.add(t);
        });
        return instances;
    }

    public T getDefaultExtension() {
        this.getExtensionClasses();
        if (StringUtils.isBlank(this.cachedDefaultName)) {
            return null;
        }
        return this.getExtension(this.cachedDefaultName);
    }

    public T getOrDefaultExtension(String name) {
        return this.containsExtension(name) ? this.getExtension(name) : this.getDefaultExtension();
    }

    private T createExtension(String name, boolean wrap) {
        Class<?> clazz = this.getExtensionClasses().get(name);
        if (clazz == null) {
            throw this.findException(name);
        }
        try {
            Object instance = EXTENSION_INSTANCES.get(clazz);
            if (instance == null) {
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.getDeclaredConstructor().newInstance());
                instance = EXTENSION_INSTANCES.get(clazz);
            }
            this.injectExtension(instance);
            if (wrap) {
                ArrayList<Object> wrapperClassesList = new ArrayList<Object>();
                if (this.wrapperExtensionLoader.getCachedWrapperClasses() != null) {
                    wrapperClassesList.addAll(this.wrapperExtensionLoader.getCachedWrapperClasses());
                    wrapperClassesList.sort(WrapperComparator.COMPARATOR);
                    Collections.reverse(wrapperClassesList);
                }
                if (CollectionUtils.isNotEmpty(wrapperClassesList)) {
                    for (Class clazz2 : wrapperClassesList) {
                        Wrapper wrapper = clazz2.getAnnotation(Wrapper.class);
                        if (wrapper != null && (!ArrayUtils.contains((Object[])wrapper.matches(), (Object)name) || ArrayUtils.contains((Object[])wrapper.mismatches(), (Object)name))) continue;
                        Object t = clazz2.getConstructor(this.type).newInstance(instance);
                        instance = this.injectExtension(t);
                    }
                }
            }
            this.initExtension(instance);
            return (T)instance;
        }
        catch (Throwable t) {
            throw new IllegalStateException("Extension instance (name: " + name + ", class: " + String.valueOf(this.type) + ") couldn't be instantiated: " + t.getMessage(), t);
        }
    }

    private boolean containsExtension(String name) {
        return this.getExtensionClasses().containsKey(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = this.cachedClasses.get();
        if (classes == null) {
            Holder<Map<String, Class<?>>> holder = this.cachedClasses;
            synchronized (holder) {
                classes = this.cachedClasses.get();
                if (classes == null) {
                    this.cacheDefaultExtensionName();
                    classes = this.loadExtensionClasses();
                    this.cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    private Map<String, Class<?>> loadExtensionClasses() {
        HashMap extensionClasses = new HashMap();
        for (LoadingStrategy strategy : LoadingStrategyHolder.strategies) {
            this.loadDirectory(extensionClasses, strategy.directory(), this.type.getName(), strategy.preferExtensionClassLoader(), strategy.overridden(), strategy.excludedPackages());
        }
        return extensionClasses;
    }

    private void loadDirectory(Map<String, Class<?>> extensionClasses, String dir, String type, boolean extensionLoaderClassLoaderFirst, boolean overridden, String ... excludedPackages) {
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
                urls = classLoader != null ? classLoader.getResources(fileName) : ClassLoader.getSystemResources(fileName);
            }
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    this.loadResource(extensionClasses, classLoader, resourceUrl, overridden, excludedPackages);
                }
            }
        }
        catch (Throwable t) {
            log.error("Exception occurred when loading extension class (interface: " + type + ", description file: " + fileName + ").", t);
        }
    }

    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL resourceURL, boolean overridden, String... excludedPackages) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceURL.openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int ci = line.indexOf('#');
                if (ci >= 0) {
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                try {
                    String clazz;
                    String name = null;
                    int i = line.indexOf('=');
                    if (i > 0) {
                        name = line.substring(0, i).trim();
                        clazz = line.substring(i + 1).trim();
                    } else {
                        clazz = line;
                    }
                    if (!StringUtils.isNotEmpty(clazz) || this.isExcluded(clazz, excludedPackages)) {
                        continue;
                    }
                    this.loadClass(extensionClasses, resourceURL, Class.forName(clazz, true, classLoader), name, overridden);
                } catch (Throwable t) {
                    IllegalStateException e = new IllegalStateException("Failed to load extension class (interface: " + this.type + ", class line: " + line + ") in " + resourceURL + ", cause: " + t.getMessage(), t);
                    this.exceptions.put(line, e);
                }
            }
        } catch (Throwable t) {
            log.error("Exception occurred when loading extension class (interface: " + this.type + ", class file: " + resourceURL + ")", t);
        }
    }

    private void loadClass(Map<String, Class<?>> extensionClasses, URL resourceURL, Class<?> clazz, String name, boolean overridden) throws NoSuchMethodException {
        if (!this.type.isAssignableFrom(clazz)) {
            throw new IllegalStateException("Error occurred when loading extension class (interface: " + String.valueOf(this.type) + ", class line: " + clazz.getName() + "), class " + clazz.getName() + " is not subtype of interface.");
        }
        if (this.isAdaptiveClass(clazz)) {
            this.adaptiveExtensionLoader.cacheAdaptiveClass(clazz, overridden);
            return;
        }
        if (this.isWrapperClass(clazz)) {
            this.wrapperExtensionLoader.cacheWrapperClass(clazz);
            return;
        }
        clazz.getConstructor();
        if (StringUtils.isEmpty(name) && (name = this.findExtensionName(clazz)).isEmpty()) {
            throw new IllegalStateException("No such extension name for the class " + clazz.getName() + " in the config " + String.valueOf(resourceURL));
        }
        Object[] names = Constants.COMMA_SPLIT_PATTERN.split(name);
        if (ArrayUtils.isNotEmpty((Object[])names)) {
            this.activeExtensionLoader.cacheActivateClass(clazz, (String)names[0]);
            for (Object n : names) {
                this.saveInCacheName(clazz, (String)n);
                this.saveInExtensionClass(extensionClasses, clazz, (String)n, overridden);
            }
        }
    }

    private void cacheDefaultExtensionName() {
        SPI defaultAnnotation = this.type.getAnnotation(SPI.class);
        if (defaultAnnotation == null) {
            return;
        }
        String value = defaultAnnotation.value().trim();
        if (!value.isEmpty()) {
            Object[] names = Constants.COMMA_SPLIT_PATTERN.split(value);
            if (names.length > 1) {
                throw new IllegalStateException("More than 1 default extension name on extension " + this.type.getName() + ": " + Arrays.toString(names));
            }
            if (names.length == 1) {
                this.cachedDefaultName = names[0];
            }
        }
    }

    private boolean isAdaptiveClass(Class<?> clazz) {
        return clazz.isAnnotationPresent(Adaptive.class);
    }

    private boolean isWrapperClass(Class<?> clazz) {
        try {
            clazz.getConstructor(this.type);
            return true;
        }
        catch (NoSuchMethodException e) {
            return false;
        }
    }

    private String findExtensionName(Class<?> clazz) {
        String name = clazz.getSimpleName();
        if (name.endsWith(this.type.getSimpleName())) {
            name = name.substring(0, name.length() - this.type.getSimpleName().length());
        }
        return name.toLowerCase();
    }

    private void saveInCacheName(Class<?> clazz, String name) {
        if (!this.cachedNames.containsKey(clazz)) {
            this.cachedNames.put(clazz, name);
        }
    }

    private void saveInExtensionClass(Map<String, Class<?>> extensionClasses, Class<?> clazz, String name, boolean overridden) {
        Class<?> c = extensionClasses.get(name);
        if (c == null || overridden) {
            extensionClasses.put(name, clazz);
        } else if (c != clazz) {
            if (clazz.isAnnotationPresent(Order.class) || c.isAnnotationPresent(Order.class)) {
                int srcValue;
                Order destOrder = clazz.getAnnotation(Order.class);
                int destValue = destOrder != null ? destOrder.value() : 0;
                Order srcOrder = c.getAnnotation(Order.class);
                int n = srcValue = srcOrder != null ? srcOrder.value() : 0;
                if (srcValue > destValue) {
                    log.debug("Compare extension " + this.type.getName() + " name " + name + " use " + clazz.getName() + " instead of " + c.getName());
                    extensionClasses.put(name, clazz);
                }
                log.debug("Compare extension " + this.type.getName() + " name " + name + " use " + c.getName() + " ignore " + clazz.getName());
                return;
            }
            String duplicateMsg = "Duplicate extension " + this.type.getName() + " name " + name + " on " + c.getName() + " and " + clazz.getName();
            log.error(duplicateMsg);
            throw new IllegalStateException(duplicateMsg);
        }
    }

    private boolean isExcluded(String className, String ... excludedPackages) {
        if (excludedPackages != null) {
            for (String excludePackage : excludedPackages) {
                if (!className.startsWith(excludePackage + ".")) continue;
                return true;
            }
        }
        return false;
    }

    private static <T> void checkExtensionAnnotation(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type == null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type (" + String.valueOf(type) + ") is not an interface!");
        }
        if (!type.isAnnotationPresent(SPI.class)) {
            throw new IllegalArgumentException("Extension type (" + String.valueOf(type) + ") is not an extension, because it is NOT annotated with @" + SPI.class.getSimpleName() + "!");
        }
    }

    private IllegalStateException findException(String name) {
        StringBuilder buf = new StringBuilder("No such extension " + this.type.getName() + " by name " + name);
        int i = 1;
        for (Map.Entry<String, IllegalStateException> entry : this.exceptions.entrySet()) {
            if (!entry.getKey().toLowerCase().startsWith(name.toLowerCase())) continue;
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
        if (i == 1) {
            buf.append(", no related exception was found, please check whether related SPI module is missing.");
        }
        return new IllegalStateException(buf.toString());
    }

    public T injectExtension(T instance) {
        if (this.objectFactory == null) {
            return instance;
        }
        try {
            for (Method method : instance.getClass().getMethods()) {
                Class<?> pt;
                if (!ReflectionUtils.isSetter(method) || ReflectionUtils.isPrimitives(pt = method.getParameterTypes()[0])) continue;
                String property = ReflectionUtils.getSetterProperty(method);
                Inject inject = method.getAnnotation(Inject.class);
                if (inject == null) {
                    this.injectValue(instance, method, pt, property);
                    continue;
                }
                if (!inject.enable()) continue;
                if (inject.type() == Inject.InjectType.ByType) {
                    this.injectValue(instance, method, pt, null);
                    continue;
                }
                this.injectValue(instance, method, pt, property);
            }
        }
        catch (Exception e) {
            log.error(e.getMessage(), (Throwable)e);
        }
        return instance;
    }

    private void injectValue(T instance, Method method, Class<?> pt, String property) {
        try {
            Object object = this.objectFactory.getExtension(pt, property);
            if (object != null) {
                method.invoke(instance, object);
            }
        }
        catch (Exception e) {
            log.error("Failed to inject via method " + method.getName() + " of interface " + this.type.getName() + ": " + e.getMessage(), (Throwable)e);
        }
    }

    private void initExtension(T instance) {
        if (instance instanceof Lifecycle) {
            Lifecycle lifecycle = (Lifecycle)instance;
            lifecycle.initialize();
        }
    }

    public ConcurrentMap<Class<?>, String> getCachedNames() {
        return this.cachedNames;
    }

    public Holder<Map<String, Class<?>>> getCachedClasses() {
        return this.cachedClasses;
    }

    public ConcurrentMap<String, Holder<Object>> getCachedInstances() {
        return this.cachedInstances;
    }

    public Map<String, IllegalStateException> getExceptions() {
        return this.exceptions;
    }

    public ActiveExtensionLoader<T> getActiveExtensionLoader() {
        return this.activeExtensionLoader;
    }

    public WrapperExtensionLoader getWrapperExtensionLoader() {
        return this.wrapperExtensionLoader;
    }

    public AdaptiveExtensionLoader<T> getAdaptiveExtensionLoader() {
        return this.adaptiveExtensionLoader;
    }

    public String getCachedDefaultName() {
        return this.cachedDefaultName;
    }

    public void setCachedDefaultName(String cachedDefaultName) {
        this.cachedDefaultName = cachedDefaultName;
    }

    public Class<?> getType() {
        return this.type;
    }

    public ExtensionFactory getObjectFactory() {
        return this.objectFactory;
    }
}

