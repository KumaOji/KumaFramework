/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.kuma.boot.common.extension.active;

import com.kuma.boot.common.extension.Activate;
import com.kuma.boot.common.extension.CollectionUtils;
import com.kuma.boot.common.extension.ExtensionLoader;
import com.kuma.boot.common.extension.active.ActivateComparator;
import com.kuma.boot.common.extension.common.Constants;
import com.kuma.boot.common.extension.common.URL;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveExtensionLoader<T> {
    private static final Logger log = LoggerFactory.getLogger(ActiveExtensionLoader.class);
    public static final String REMOVE_VALUE_PREFIX = "-";
    public static final String DEFAULT_KEY = "default";
    private final Map<String, Object> cachedActivates = new ConcurrentHashMap<String, Object>();
    private final ExtensionLoader<T> extensionLoader;

    public ActiveExtensionLoader(ExtensionLoader<T> extensionLoader) {
        this.extensionLoader = extensionLoader;
    }

    public void cacheActivateClass(Class<?> clazz, String name) {
        Activate activate = clazz.getAnnotation(Activate.class);
        if (activate != null) {
            this.cachedActivates.put(name, activate);
        }
    }

    public List<T> getActivateExtension(URL url, String[] values, String group) {
        ArrayList<Object> activateExtensions = new ArrayList<Object>();
        TreeMap<Object, T> activateExtensionsMap = new TreeMap<Object, T>(ActivateComparator.COMPARATOR);
        HashSet<String> loadedNames = new HashSet<String>();
        Set<String> names = CollectionUtils.ofSet(values);
        if (!names.contains("-default")) {
            this.extensionLoader.getExtensionClasses();
            for (Map.Entry<String, Object> entry : this.cachedActivates.entrySet()) {
                String name = entry.getKey();
                Object activate = entry.getValue();
                if (!(activate instanceof Activate)) continue;
                String[] activateGroup = ((Activate)activate).group();
                String[] activateValue = ((Activate)activate).value();
                if (!this.isMatchGroup(group, activateGroup) || names.contains(name) || names.contains(REMOVE_VALUE_PREFIX + name) || !this.isActive(activateValue, url) || loadedNames.contains(name)) continue;
                activateExtensionsMap.put(this.extensionLoader.getExtensionClass(name), this.extensionLoader.getExtension(name));
                loadedNames.add(name);
            }
            if (!activateExtensionsMap.isEmpty()) {
                activateExtensions.addAll(activateExtensionsMap.values());
            }
        }
        ArrayList<T> loadedExtensions = new ArrayList<T>();
        for (String name : names) {
            if (name.startsWith(REMOVE_VALUE_PREFIX) || names.contains(REMOVE_VALUE_PREFIX + name)) continue;
            if (!loadedNames.contains(name)) {
                if (DEFAULT_KEY.equals(name)) {
                    if (!loadedExtensions.isEmpty()) {
                        activateExtensions.addAll(0, loadedExtensions);
                        loadedExtensions.clear();
                    }
                } else {
                    loadedExtensions.add(this.extensionLoader.getExtension(name));
                }
                loadedNames.add(name);
                continue;
            }
            String simpleName = this.extensionLoader.getExtensionClass(name).getSimpleName();
            log.warn("Catch duplicated filter, ExtensionLoader will ignore one of them. Please check. Filter Name: " + name + ". Ignored Class Name: " + simpleName);
        }
        if (!loadedExtensions.isEmpty()) {
            activateExtensions.addAll(loadedExtensions);
        }
        return activateExtensions;
    }

    public List<T> getActivateExtension(URL url, String key) {
        return this.getActivateExtension(url, key, null);
    }

    public List<T> getActivateExtension(URL url, String key, String group) {
        String value = url.getParameter(key);
        return this.getActivateExtension(url, StringUtils.isEmpty(value) ? null : Constants.COMMA_SPLIT_PATTERN.split(value), group);
    }

    public List<T> getActivateExtension(URL url, String[] values) {
        return this.getActivateExtension(url, values, null);
    }

    private boolean isMatchGroup(String group, String[] groups) {
        if (StringUtils.isEmpty(group)) {
            return true;
        }
        if (groups != null && groups.length > 0) {
            for (String g : groups) {
                if (!group.equals(g)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean isActive(String[] keys, URL url) {
        if (keys.length == 0) {
            return true;
        }
        for (String key : keys) {
            String keyValue = null;
            if (!key.contains(":")) continue;
            String[] arr = key.split(":");
            key = arr[0];
            keyValue = arr[1];
        }
        return false;
    }
}

