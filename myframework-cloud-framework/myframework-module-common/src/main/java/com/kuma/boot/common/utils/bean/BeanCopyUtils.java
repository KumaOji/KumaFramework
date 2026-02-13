/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  org.springframework.cglib.beans.BeanCopier
 */
package com.kuma.boot.common.utils.bean;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.cglib.beans.BeanCopier;

public final class BeanCopyUtils {
    private static LoadingCache<ClassTuple, BeanCopier> cache = CacheBuilder.newBuilder().maximumSize(1024L).build((CacheLoader)new CacheLoader<ClassTuple, BeanCopier>(){

        public BeanCopier load(ClassTuple classTuple) {
            return BeanCopier.create(classTuple.sourceClass, classTuple.targetClass, (boolean)false);
        }
    });

    private BeanCopyUtils() {
    }

    public static void copyProperties(Object source, Object target) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        try {
            BeanCopier beanCopier = (BeanCopier)cache.get((ClassTuple) new ClassTuple(source.getClass(), target.getClass()));
            beanCopier.copy(source, target, null);
        }
        catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T copyPropertiesAndGet(Object source, T target) {
        BeanCopyUtils.copyProperties(source, target);
        return target;
    }

    public static <T> T copyPropertiesAndGet(Object source, Supplier<T> supplier) {
        return BeanCopyUtils.copyPropertiesAndGet(source, supplier.get());
    }

    public static <S, CS extends Collection<? extends S>, T, CT extends Collection<T>> CT copyCollection(CS source, Supplier<CT> collectionSupplier, Supplier<T> elementSupplier) {
        return (CT)((Collection)source.stream().map(x -> BeanCopyUtils.copyPropertiesAndGet(x, elementSupplier)).collect(Collectors.toCollection(collectionSupplier)));
    }

    public static <S, CS extends Collection<? extends S>, T> List<T> copyList(CS source, Supplier<T> elementSupplier) {
        return BeanCopyUtils.copyCollection(source, ArrayList::new, elementSupplier);
    }

    public static <S, CS extends Collection<? extends S>, T> Set<T> copySet(CS source, Supplier<T> elementSupplier) {
        return BeanCopyUtils.copyCollection(source, HashSet::new, elementSupplier);
    }

    private static class ClassTuple {
        private Class<?> sourceClass;
        private Class<?> targetClass;

        public ClassTuple(Class<?> sourceClass, Class<?> targetClass) {
            this.sourceClass = sourceClass;
            this.targetClass = targetClass;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ClassTuple that = (ClassTuple)o;
            return com.google.common.base.Objects.equal(this.sourceClass, that.sourceClass) && com.google.common.base.Objects.equal(this.targetClass, that.targetClass);
        }

        public int hashCode() {
            return com.google.common.base.Objects.hashCode((Object[])new Object[]{this.sourceClass, this.targetClass});
        }
    }
}

