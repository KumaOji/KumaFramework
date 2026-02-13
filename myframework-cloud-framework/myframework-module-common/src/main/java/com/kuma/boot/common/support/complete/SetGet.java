/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.complete;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SetGet<E, I, N> {
    private final Function<? super E, ? extends I> idGetter;
    private final BiConsumer<? super E, ? super N> nameSetter;

    public SetGet(Function<? super E, ? extends I> idGetter, BiConsumer<? super E, ? super N> nameSetter) {
        assert (idGetter != null) : "idGetter must not be null";
        assert (nameSetter != null) : "nameSetter must not be null";
        this.idGetter = idGetter;
        this.nameSetter = nameSetter;
    }

    public I get(E target) {
        return this.idGetter.apply(target);
    }

    public void set(E target, N value) {
        this.nameSetter.accept(target, value);
    }
}

