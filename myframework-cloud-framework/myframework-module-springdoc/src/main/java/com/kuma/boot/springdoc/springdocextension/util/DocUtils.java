/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.enums.base.CodeEnum
 *  com.kuma.boot.common.enums.base.CommonEnum
 *  com.kuma.boot.common.enums.base.SelfDescribedEnum
 *  org.springframework.web.bind.annotation.RestController
 */
package com.kuma.boot.springdoc.springdocextension.util;

import com.kuma.boot.common.enums.base.CodeEnum;
import com.kuma.boot.common.enums.base.CommonEnum;
import com.kuma.boot.common.enums.base.SelfDescribedEnum;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.RestController;

public class DocUtils {
    private DocUtils() {
    }

    public static String getEnumValueTypeAsString(Class<?> enumClass) {
        Type[] interfaces = enumClass.getGenericInterfaces();
        Map<Class<String>, String> typeMap = Map.of(Integer.class, "integer", Long.class, "long", Double.class, "number", String.class, "string");
        for (Type type : interfaces) {
            Type actualType;
            ParameterizedType parameterizedType;
            if (!(type instanceof ParameterizedType) || (parameterizedType = (ParameterizedType)type).getRawType() != CommonEnum.class || !((actualType = parameterizedType.getActualTypeArguments()[0]) instanceof Class)) continue;
            Class actualClass = (Class)actualType;
            return typeMap.getOrDefault(actualClass, "string");
        }
        return "string";
    }

    public static String resolveFormat(String enumValueType) {
        return switch (enumValueType) {
            case "integer" -> "int32";
            case "long" -> "int64";
            case "number" -> "double";
            default -> enumValueType;
        };
    }

    public static boolean hasRestControllerAnnotation(Class<?> clazz) {
        if (clazz.isAnnotationPresent(RestController.class)) {
            return true;
        }
        for (Class<?> superClass = clazz.getSuperclass(); superClass != null && !superClass.equals(Object.class); superClass = superClass.getSuperclass()) {
            if (!DocUtils.hasRestControllerAnnotation(superClass)) continue;
            return true;
        }
        return false;
    }

    public static Map<Object, String> getDescMap(Class<?> enumClass) {
        CommonEnum[] enums = (CommonEnum[])enumClass.getEnumConstants();
        return Arrays.stream(enums).collect(Collectors.toMap(CodeEnum::getCode, SelfDescribedEnum::getDesc, (a, b) -> a, LinkedHashMap::new));
    }
}

