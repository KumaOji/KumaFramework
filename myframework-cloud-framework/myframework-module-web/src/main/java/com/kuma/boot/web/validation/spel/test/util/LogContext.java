/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.slf4j.MDC
 */
package com.kuma.boot.web.validation.spel.test.util;

import org.slf4j.MDC;

public class LogContext {
    public static void setValidateObject(Object object) {
        Object className = object.getClass().getSimpleName();
        Class<?> enclosingClass = object.getClass().getEnclosingClass();
        if (enclosingClass != null) {
            className = enclosingClass.getSimpleName() + "." + (String)className;
        }
        MDC.put((String)"className", (String)className);
        MDC.put((String)"fullClassName", (String)LogContext.abbreviate(object.getClass().getName()));
        if (object instanceof ID) {
            MDC.put((String)"id", (String)String.valueOf(((ID)object).getId()));
        }
    }

    public static void clearValidateObject() {
        MDC.remove((String)"id");
        MDC.remove((String)"className");
        MDC.remove((String)"fieldName");
        MDC.remove((String)"fullClassName");
    }

    public static void set(String key, String value) {
        MDC.put((String)key, (String)value);
    }

    public static void remove(String key) {
        MDC.remove((String)key);
    }

    private static String abbreviate(String className) {
        String[] parts = className.split("\\.");
        StringBuilder abbreviated = new StringBuilder();
        for (int i = 0; i < parts.length - 1; ++i) {
            abbreviated.append(parts[i].charAt(0)).append(".");
        }
        abbreviated.append(parts[parts.length - 1]);
        return abbreviated.toString();
    }
}

