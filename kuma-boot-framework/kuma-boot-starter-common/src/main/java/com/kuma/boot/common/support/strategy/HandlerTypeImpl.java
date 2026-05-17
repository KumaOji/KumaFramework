package com.kuma.boot.common.support.strategy;

import java.lang.annotation.Annotation;

public class HandlerTypeImpl implements HandlerType {

    private final String type;
    private final String source;

    public HandlerTypeImpl(String type, String source) {
        this.type = type;
        this.source = source;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode += (127 * "type".hashCode()) ^ type.hashCode();
        hashCode += (127 * "source".hashCode()) ^ source.hashCode();
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HandlerType other)) {
            return false;
        }
        return type.equals(other.type()) && source.equals(other.source());
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return HandlerType.class;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String source() {
        return source;
    }
}
