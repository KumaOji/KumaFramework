/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public class ParameterizedTypeImpl
implements ParameterizedType {
    private final Type[] actualTypeArguments;
    private final Type ownerType;
    private final Type rawType;

    public ParameterizedTypeImpl(Type[] actualTypeArguments, Type ownerType, Type rawType) {
        this.actualTypeArguments = actualTypeArguments;
        this.ownerType = ownerType;
        this.rawType = rawType;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return this.actualTypeArguments;
    }

    @Override
    public Type getOwnerType() {
        return this.ownerType;
    }

    @Override
    public Type getRawType() {
        return this.rawType;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ParameterizedTypeImpl that = (ParameterizedTypeImpl)o;
        if (!Arrays.equals(this.actualTypeArguments, that.actualTypeArguments)) {
            return false;
        }
        if (this.ownerType != null ? !this.ownerType.equals(that.ownerType) : that.ownerType != null) {
            return false;
        }
        return this.rawType != null ? this.rawType.equals(that.rawType) : that.rawType == null;
    }

    public int hashCode() {
        int result = this.actualTypeArguments != null ? Arrays.hashCode(this.actualTypeArguments) : 0;
        result = 31 * result + (this.ownerType != null ? this.ownerType.hashCode() : 0);
        result = 31 * result + (this.rawType != null ? this.rawType.hashCode() : 0);
        return result;
    }
}

