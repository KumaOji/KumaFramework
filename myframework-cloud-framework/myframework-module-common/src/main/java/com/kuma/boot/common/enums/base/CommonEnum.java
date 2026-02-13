/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.enums.base;

import com.kuma.boot.common.enums.base.CodeEnum;
import com.kuma.boot.common.enums.base.SelfDescribedEnum;
import java.util.Objects;

public interface CommonEnum extends CodeEnum,
SelfDescribedEnum {
    default public boolean match(String value) {
        if (value == null) {
            return false;
        }
        return value.equals(String.valueOf(this.getCode())) || value.equals(this.getName());
    }

    public static <E extends Enum<E>, T> E getByValue(T value, Class<E> clazz) {
        for (Enum e : (Enum[])clazz.getEnumConstants()) {
            if (!Objects.equals(((CodeEnum)((Object)e)).getCode(), value) && !Objects.equals(((SelfDescribedEnum)((Object)e)).getName(), value)) continue;
            return (E)e;
        }
        return null;
    }

    public static <E extends Enum<E>> E getByDescription(String description, Class<?> clazz) {
        for (Object e : clazz.getEnumConstants()) {
            CommonEnum baseEnum;
            if (!(e instanceof CommonEnum) || !Objects.equals((baseEnum = (CommonEnum)e).getDesc(), description)) continue;
            Enum anEnum = (Enum)((Object)baseEnum);
            return (E)anEnum;
        }
        return null;
    }

    public static <E extends Enum<E>, T> boolean isValidValue(T value, Class<E> clazz) {
        return CommonEnum.getByValue(value, clazz) != null;
    }
}

