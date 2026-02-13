/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.complete;

import com.kuma.boot.common.support.complete.EmptyUtil;
import com.kuma.boot.common.support.complete.Prepare;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Complete<E> {
    private final List<Prepare<?, ?, E>> actuator = new ArrayList();
    private final Collection<E> collection;

    private Complete(Collection<E> collection) {
        this.collection = collection;
    }

    public static <E> Complete<E> start(Collection<E> collection) {
        return new Complete<E>(collection);
    }

    public <I, N> Prepare<I, N, E> build(Function<? super List<I>, ? extends Map<? super I, ? extends N>> nameMapCreator) {
        Prepare prepare = new Prepare(nameMapCreator, this);
        this.actuator.add(prepare);
        return prepare;
    }

    public <I, N> Prepare<I, N, E> build(Function<? super E, ? extends I> idGetter, BiConsumer<? super E, ? super N> nameSetter, Function<? super List<I>, ? extends Map<? super I, ? extends N>> nameMapCreator) {
        Prepare<? extends I, ? super N, ? super E> prepare = new Prepare<I, N, E>(nameMapCreator, this);
        prepare.add(idGetter, nameSetter);
        this.actuator.add(prepare);
        return prepare;
    }

    public Complete<E> run() {
        return this.finish(null);
    }

    public Complete<E> finish(Executor executor) {
        this.over(executor);
        return this;
    }

    public void over() {
        this.over(null);
    }

    public void over(Executor executor) {
        if (EmptyUtil.isEmpty(this.collection) || EmptyUtil.isEmpty(this.actuator)) {
            this.actuator.clear();
            return;
        }
        this.collection.forEach(item -> this.actuator.forEach(prepare -> prepare.init(item)));
        if (executor == null) {
            this.actuator.stream().map(Prepare::finish).reduce(Consumer::andThen).ifPresent(this.collection::forEach);
        } else {
            List futures = this.actuator.stream().map(prepare -> CompletableFuture.supplyAsync(prepare::finish, executor)).collect(Collectors.toList());
            futures.stream().map(CompletableFuture::join).reduce(Consumer::andThen).ifPresent(this.collection::forEach);
        }
        this.actuator.clear();
    }
}

