/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.extension.active;

import com.kuma.boot.common.extension.Activate;
import com.kuma.boot.common.extension.CollectionUtils;
import com.kuma.boot.common.extension.ExtensionLoader;
import com.kuma.boot.common.extension.common.Constants;
import com.kuma.boot.common.extension.common.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 基于 @Active 的扩展点加载器 */
public class ActiveExtensionLoader<T> {

    private static final Logger log = LoggerFactory.getLogger(ActiveExtensionLoader.class);

    public static final String REMOVE_VALUE_PREFIX = "-";

    public static final String DEFAULT_KEY = "default";

    /** 缓存激活的扩展点 */
    private final Map<String, Object> cachedActivates = new ConcurrentHashMap<>();

    private final ExtensionLoader<T> extensionLoader;

    public ActiveExtensionLoader(ExtensionLoader<T> extensionLoader) {
        this.extensionLoader = extensionLoader;
    }

    /**
     * 缓存 @Activate 扩展类
     * @param clazz
     * @param name
     */
    public void cacheActivateClass(Class<?> clazz, String name) {
        Activate activate = clazz.getAnnotation(Activate.class);
        if (activate != null) {
            cachedActivates.put(name, activate);
        }
    }

    /**
     * 获取 @Activate 扩展点
     * @param url
     * @param values
     * @param group
     * @return
     */
    public List<T> getActivateExtension(URL url, String[] values, String group) {
        List<T> activateExtensions = new ArrayList<>();
        TreeMap<Class<?>, T> activateExtensionsMap = new TreeMap<>(ActivateComparator.COMPARATOR);
        Set<String> loadedNames = new HashSet<>();
        Set<String> names = CollectionUtils.ofSet(values);
        if (!names.contains(REMOVE_VALUE_PREFIX + DEFAULT_KEY)) {
            extensionLoader.getExtensionClasses();
            for (Map.Entry<String, Object> entry : cachedActivates.entrySet()) {
                String name = entry.getKey();
                Object activate = entry.getValue();
                String[] activateGroup;
                String[] activateValue;
                if (activate instanceof Activate) {
                    activateGroup = ((Activate) activate).group();
                    activateValue = ((Activate) activate).value();
                } else {
                    continue;
                }
                if (isMatchGroup(group, activateGroup)
                        && !names.contains(name)
                        && !names.contains(REMOVE_VALUE_PREFIX + name)
                        && isActive(activateValue, url)
                        && !loadedNames.contains(name)) {
                    activateExtensionsMap.put(
                            extensionLoader.getExtensionClass(name),
                            extensionLoader.getExtension(name));
                    loadedNames.add(name);
                }
            }
            if (!activateExtensionsMap.isEmpty()) {
                activateExtensions.addAll(activateExtensionsMap.values());
            }
        }
        List<T> loadedExtensions = new ArrayList<>();
        for (String name : names) {
            if (!name.startsWith(REMOVE_VALUE_PREFIX)
                    && !names.contains(REMOVE_VALUE_PREFIX + name)) {
                if (!loadedNames.contains(name)) {
                    if (DEFAULT_KEY.equals(name)) {
                        if (!loadedExtensions.isEmpty()) {
                            activateExtensions.addAll(0, loadedExtensions);
                            loadedExtensions.clear();
                        }
                    } else {
                        loadedExtensions.add(extensionLoader.getExtension(name));
                    }
                    loadedNames.add(name);
                } else {
                    String simpleName = extensionLoader.getExtensionClass(name).getSimpleName();
                    log.warn(
                            "Catch duplicated filter, ExtensionLoader will ignore one of them."
                                    + " Please check. Filter Name: "
                                    + name
                                    + ". Ignored Class Name: "
                                    + simpleName);
                }
            }
        }
        if (!loadedExtensions.isEmpty()) {
            activateExtensions.addAll(loadedExtensions);
        }
        return activateExtensions;
    }

    public List<T> getActivateExtension(URL url, String key) {
        return getActivateExtension(url, key, null);
    }

    public List<T> getActivateExtension(URL url, String key, String group) {
        String value = url.getParameter(key);
        return getActivateExtension(
                url,
                StringUtils.isEmpty(value) ? null : Constants.COMMA_SPLIT_PATTERN.split(value),
                group);
    }

    public List<T> getActivateExtension(URL url, String[] values) {
        return getActivateExtension(url, values, null);
    }

    private boolean isMatchGroup(String group, String[] groups) {
        if (StringUtils.isEmpty(group)) {
            return true;
        }
        if (groups != null && groups.length > 0) {
            for (String g : groups) {
                if (group.equals(g)) {
                    return true;
                }
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
            if (key.contains(":")) {
                String[] arr = key.split(":");
                key = arr[0];
                keyValue = arr[1];
            }

            /*
             * for (Map.Entry<String, String> entry : url.getParameters().entrySet()) {
             * String k = entry.getKey(); String v = entry.getValue(); if ((k.equals(key)
             * || k.endsWith("." + key)) && ((keyValue != null && keyValue.equals(v)) ||
             * (keyValue == null && ConfigUtils.isNotEmpty(v)))) { return true; } }
             */
        }
        return false;
    }
}
