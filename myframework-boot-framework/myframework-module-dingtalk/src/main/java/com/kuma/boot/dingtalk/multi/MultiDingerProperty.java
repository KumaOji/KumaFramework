/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.multi;

import com.kuma.boot.dingtalk.enums.IgnoreMethod;
import java.util.HashMap;
import java.util.Map;

public class MultiDingerProperty {
    static boolean multiDinger = false;
    protected static Map<String, IgnoreMethod> ignoreMethodMap = new HashMap<String, IgnoreMethod>();

    protected static boolean multiDinger() {
        return multiDinger;
    }

    protected static void clear() {
        ignoreMethodMap.clear();
    }

    static {
        for (IgnoreMethod ignoreMethod : IgnoreMethod.values()) {
            ignoreMethodMap.put(ignoreMethod.getMethodName(), ignoreMethod);
        }
    }
}

