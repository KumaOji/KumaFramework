/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.deepcopy;

import com.kuma.boot.common.support.deepcopy.DeepCopy;

public abstract class AbstractDeepCopy
implements DeepCopy {
    protected abstract <T> T doDeepCopy(T var1);

    @Override
    public <T> T deepCopy(T object) {
        if (null == object) {
            return null;
        }
        return this.doDeepCopy(object);
    }
}

