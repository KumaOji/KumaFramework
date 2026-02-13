/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.enums;

public final class EnumUtils {
    private EnumUtils() {
    }

    public static <T extends Enum<?>> T lookup(Class<T> enumType, String name) {
        for (Enum t : (Enum[])enumType.getEnumConstants()) {
            if (!t.name().equalsIgnoreCase(name)) continue;
            return (T)t;
        }
        return null;
    }
}

