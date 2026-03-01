package com.kuma.boot.web.validation.spel.core.exception;

import java.util.Set;

/**
 * 不支持的类型异常
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/5/3
 */
public class SpelNotSupportedTypeException extends SpelValidatorException {

    private final Class<?> clazz;

    private final Set<Class<?>> supperType;

    public Class<?> getClazz() {
        return clazz;
    }

    public Set<Class<?>> getSupperType() {
        return supperType;
    }

    public SpelNotSupportedTypeException(Class<?> clazz, Set<Class<?>> supperType) {
        super("Class type not supported, current type: " + clazz.getName() + ", supper type: " + supperType.toString());
        this.clazz = clazz;
        this.supperType = supperType;
    }

}
