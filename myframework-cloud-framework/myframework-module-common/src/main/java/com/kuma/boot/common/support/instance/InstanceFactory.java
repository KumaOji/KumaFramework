/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.instance;

import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.support.instance.Instance;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class InstanceFactory
implements Instance {
    private final Map<String, Object> singletonMap = new ConcurrentHashMap<String, Object>();
    private ThreadLocal<Map<String, Object>> mapThreadLocal = new ThreadLocal();

    private InstanceFactory() {
    }

    public static InstanceFactory getInstance() {
        return SingletonHolder.INSTANCE_FACTORY;
    }

    public static <T> T singletion(Class<T> tClass) {
        return InstanceFactory.getInstance().singleton(tClass);
    }

    public static <T> T singletion(Class<T> tClass, String groupName) {
        return InstanceFactory.getInstance().singleton(tClass, groupName);
    }

    @Override
    public <T> T singleton(Class<T> tClass, String groupName) {
        return this.getSingleton(tClass, groupName, this.singletonMap);
    }

    @Override
    public <T> T singleton(Class<T> tClass) {
        this.notNull(tClass);
        return this.getSingleton(tClass, this.singletonMap);
    }

    @Override
    public <T> T threadLocal(Class<T> tClass) {
        this.notNull(tClass);
        Map<String, Object> map = this.mapThreadLocal.get();
        if (ObjectUtils.isNull(map)) {
            map = new ConcurrentHashMap<String, Object>();
        }
        T instance = this.getSingleton(tClass, map);
        this.mapThreadLocal.set(map);
        return instance;
    }

    @Override
    public <T> T multiple(Class<T> tClass) {
        this.notNull(tClass);
        try {
            return tClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException e) {
            throw new BootException(e);
        }
        catch (InvocationTargetException e) {
            LogUtils.error(e);
            throw new BootException("\u7c7b\u578b\u5f02\u5e38");
        }
    }

    @Override
    public <T> T threadSafe(Class<T> tClass) {
        return this.singleton(tClass);
    }

    private <T> T getSingleton(Class<T> tClass, Map<String, Object> instanceMap) {
        this.notNull(tClass);
        String fullClassName = tClass.getName();
        Object instance = instanceMap.get(fullClassName);
        if (ObjectUtils.isNull(instance)) {
            instance = this.multiple(tClass);
            instanceMap.put(fullClassName, instance);
        }
        return (T)instance;
    }

    private <T> T getSingleton(Class<T> tClass, String group, Map<String, Object> instanceMap) {
        this.notNull(tClass);
        ArgUtils.notEmpty(group, "key");
        String fullClassName = tClass.getName() + "-" + group;
        Object instance = instanceMap.get(fullClassName);
        if (ObjectUtils.isNull(instance)) {
            instance = this.multiple(tClass);
            instanceMap.put(fullClassName, instance);
        }
        return (T)instance;
    }

    private void notNull(Class tClass) {
        ArgUtils.notNull(tClass, "class");
    }

    private static class SingletonHolder {
        private static final InstanceFactory INSTANCE_FACTORY = new InstanceFactory();

        private SingletonHolder() {
        }
    }
}

