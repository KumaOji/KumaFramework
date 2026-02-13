/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.proxy;

import com.kuma.boot.common.enums.ProxyTypeEnum;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import java.lang.reflect.Proxy;

public class ProxyFactory {
    private ProxyFactory() {
    }

    public static ProxyTypeEnum getProxyType(Object object) {
        if (ObjectUtils.isNull(object)) {
            return ProxyTypeEnum.NONE;
        }
        Class<?> clazz = object.getClass();
        if (clazz.isInterface() || Proxy.isProxyClass(clazz)) {
            return ProxyTypeEnum.JDK;
        }
        return ProxyTypeEnum.CGLIB;
    }
}

