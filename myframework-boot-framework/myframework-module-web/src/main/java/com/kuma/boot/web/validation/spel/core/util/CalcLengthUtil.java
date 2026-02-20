/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.validation.spel.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CalcLengthUtil {
    public static final Set<Class<?>> SUPPORT_TYPE;

    private CalcLengthUtil() {
    }

    public static int calcFieldSize(Object object) {
        if (object instanceof CharSequence) {
            return ((CharSequence)object).length();
        }
        if (object instanceof Collection) {
            return ((Collection)object).size();
        }
        if (object instanceof Map) {
            return ((Map)object).size();
        }
        if (object instanceof Object[]) {
            return ((Object[])object).length;
        }
        return 0;
    }

    static {
        HashSet<Class> hashSet = new HashSet<Class>();
        hashSet.add(CharSequence.class);
        hashSet.add(Collection.class);
        hashSet.add(Map.class);
        hashSet.add(Object[].class);
        SUPPORT_TYPE = Collections.unmodifiableSet(hashSet);
    }
}

