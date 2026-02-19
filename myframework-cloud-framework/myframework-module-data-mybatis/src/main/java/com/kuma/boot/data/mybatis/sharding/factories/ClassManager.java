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

package com.kuma.boot.data.mybatis.sharding.factories;

import com.kuma.boot.data.mybatis.sharding.annos.Sharding;
import com.kuma.boot.data.mybatis.sharding.utils.ClassScanUtils;
import com.kuma.boot.data.mybatis.sharding.utils.NameUtils;
import com.kuma.boot.data.mybatis.sharding.utils.ResourceUtil;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * mapper类管理器， 方法管理
 * 生成的类全部由此管理
 *
 * @author winjeg
 */
public class ClassManager {
    private static final Map<String, Map<String, Class<?>>> CLASS_MAP = new HashMap<>();

    /**
     * KEY1： dsName， Key2: Interface.class  Key3 方法名
     */
    private static final Map<String, Map<String, Map<String, Method>>> METHOD_CACHE =
            new HashMap<>();

    private final List<Class<?>> classes;

    public ClassManager(List<Class<?>> cs) {
        classes = cs;
        init();
    }

    private void init() {
        for (Class<?> clz : classes) {
            Sharding sharding = ResourceUtil.getShardingAnno(clz);
            if (!sharding.tableRule().isEmpty()) {
                for (String dsName : sharding.datasource()) {
                    generate(dsName, clz, true);
                }
            } else if (!sharding.dbRule().isEmpty()) {
                for (String dsName : sharding.datasource()) {
                    generate(dsName, clz, false);
                }
            }
        }
    }

    private void generate(String dsName, Class<?> clz, boolean sharding) {
        Map<String, Class<?>> dsMap = CLASS_MAP.get(dsName);
        if (dsMap == null) {
            dsMap = new HashMap<>();
        }
        Class<?> genClz = ClassScanUtils.generateMapperViaMapper(clz, dsName, sharding);
        String name = NameUtils.buildClassName(dsName, clz.getCanonicalName());
        dsMap.put(name, genClz);
        CLASS_MAP.put(dsName, dsMap);

        Map<String, Map<String, Method>> dsMethodMap = METHOD_CACHE.get(dsName);
        if (dsMethodMap == null) {
            dsMethodMap = new HashMap<>();
        }
        Map<String, Method> methodMap = dsMethodMap.get(name);
        if (methodMap == null) {
            methodMap = new HashMap<>();
        }
        for (Method m : genClz.getDeclaredMethods()) {
            methodMap.put(m.getName(), m);
        }
        dsMethodMap.put(name, methodMap);
        METHOD_CACHE.put(dsName, dsMethodMap);
    }

    /**
     * 根据数据源和类名获取对应的类
     *
     * @param dsName    数据源
     * @param className 类名
     * @return 类
     */
    public Class<?> getClass(String dsName, String className) {
        return CLASS_MAP.get(dsName).get(className);
    }

    /**
     * 根据数据源和类名方法名获取对应的方法
     *
     * @param dsName    数据源
     * @param className 类名
     * @param name      方法名
     * @return 类
     */
    public Method getMethod(String dsName, String className, String name) {
        return METHOD_CACHE.get(dsName).get(className).get(name);
    }
}
