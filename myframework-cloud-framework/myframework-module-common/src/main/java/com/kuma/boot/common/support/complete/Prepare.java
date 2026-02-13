/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.complete;

import com.kuma.boot.common.support.complete.Complete;
import com.kuma.boot.common.support.complete.EmptyUtil;
import com.kuma.boot.common.support.complete.SetGet;
import com.kuma.boot.common.support.complete.Write;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Prepare<I, N, E> {
    private volatile Set<SetGet<E, I, N>> setGetList;
    private volatile Set<SetGet<E, List<I>, List<N>>> collSetGetList;
    private final Write<I, N> write;
    private final AtomicBoolean isPrepare = new AtomicBoolean(false);
    private final Complete<E> father;
    private Predicate<E> filter = Objects::nonNull;

    protected Prepare(Function<? super List<I>, ? extends Map<? super I, ? extends N>> nameMapCreator, Complete<E> father) {
        assert (nameMapCreator != null) : "nameMapCreator can not be null!";
        this.write = new Write(nameMapCreator);
        this.father = father;
    }

    public Complete<E> then() {
        return this.father;
    }

    public Complete<E> doThen() {
        return this.father.run();
    }

    public Complete<E> doThen(Executor executor) {
        return this.father.finish(executor);
    }

    public Prepare<I, N, E> filter(Predicate<E> filter) {
        if (filter != null) {
            this.filter = this.filter.and(filter);
        }
        return this;
    }

    public Prepare<I, N, E> add(Function<? super E, ? extends I> idGetter, BiConsumer<? super E, ? super N> nameSetter) {
        return this.add(new SetGet<E, I, N>(idGetter, nameSetter));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Prepare<I, N, E> add(SetGet<E, I, N> setGet) {
        if (setGet == null) {
            return this;
        }
        if (this.setGetList == null) {
            Prepare prepare = this;
            synchronized (prepare) {
                this.setGetList = new HashSet<SetGet<E, I, N>>();
            }
        }
        this.setGetList.add(setGet);
        return this;
    }

    public Prepare<I, N, E> addColl(Function<? super E, List<I>> idGetter, BiConsumer<? super E, List<N>> nameSetter) {
        return this.addColl(new SetGet<E, List<I>, List<N>>(idGetter, nameSetter));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Prepare<I, N, E> addColl(SetGet<E, List<I>, List<N>> setGet) {
        if (setGet == null) {
            return this;
        }
        if (this.collSetGetList == null) {
            Prepare prepare = this;
            synchronized (prepare) {
                if (this.collSetGetList == null) {
                    this.collSetGetList = new HashSet<SetGet<E, List<I>, List<N>>>();
                }
            }
        }
        this.collSetGetList.add(setGet);
        return this;
    }

    protected void init(E target) {
        if (this.filter.test(target)) {
            if (EmptyUtil.isNotEmpty(this.collSetGetList)) {
                this.collSetGetList.stream()
                        .filter(s -> s.get(target) != null)
                        .flatMap(s -> ((List<?>) s.get(target)).stream())
                        .forEach(this.write::add);
            }
            if (EmptyUtil.isNotEmpty(this.setGetList)) {
                this.setGetList.stream().map(s -> s.get(target)).forEach(this.write::add);
            }
            this.isPrepare.set(true);
        }
    }

    protected Consumer<E> finish() {
        if (!this.isPrepare.get()) {
            return target -> {};
        }
        Map map = this.write.get();
        return target -> {
            if (!this.filter.test(target) || EmptyUtil.isEmpty(map)) {
                return;
            }
            if (EmptyUtil.isNotEmpty(this.setGetList)) {
                this.setGetList.forEach(s -> {
                    Object n = map.get(s.get(target));
                    if (n != null) {
                        s.set(target, n);
                    }
                });
            }
            if (EmptyUtil.isNotEmpty(this.collSetGetList)) {
                this.collSetGetList.forEach(s -> {
                    List ids = (List)s.get(target);
                    if (EmptyUtil.isNotEmpty(ids)) {
                        List names = ids.stream().map(map::get).filter(Objects::nonNull).collect(Collectors.toList());
                        s.set(target, names);
                    }
                });
            }
        };
    }
}

