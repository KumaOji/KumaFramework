/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.support.aop;

import com.google.common.collect.Sets;
import com.kuma.boot.common.utils.reflect.AnnotationUtils;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

/**
 * @author livk
 */
final class AnnotationTarget<A extends Annotation> {

    public static final AnnotationAutoPointcut POINTCUT = new AnnotationTargetPointcut();

    private static final ConcurrentMap<Class<? extends Annotation>, AnnotationTarget<?>> CACHE =
            new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> AnnotationTarget<A> of( Class<A> annotationType ) {
        return (AnnotationTarget<A>) CACHE.computeIfAbsent(annotationType, AnnotationTarget::new);
    }

    private final Class<A> annotationType;

    private final Set<ElementType> elementTypes;

    private AnnotationTarget( Class<A> annotationType ) {
        this.annotationType = annotationType;
        Target target = annotationType.getAnnotation(Target.class);
        this.elementTypes = Sets.newHashSet(target.value());
    }

    public A getAnnotation( Method method ) {
        return supportMethod() ? AnnotationUtils.getAnnotation(method, annotationType) : null;
    }

    public A getAnnotation( Class<?> clazz ) {
        return supportType() ? AnnotationUtils.getAnnotation(clazz, annotationType) : null;
    }

    private boolean supportMethod() {
        return elementTypes.contains(ElementType.METHOD);
    }

    private boolean supportType() {
        return elementTypes.contains(ElementType.TYPE);
    }

    /**
     * AnnotationTargetPointcut
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-17 10:30:45
     */
    static final class AnnotationTargetPointcut implements AnnotationAutoPointcut {

        @Override
        public Pointcut getPointcut( Class<? extends Annotation> annotationType ) {
            AnnotationTarget<?> target = AnnotationTarget.of(annotationType);
            if (target.supportType() && target.supportMethod()) {
                return new AnnotationClassOrMethodPointcut(annotationType);
            } else if (target.supportType()) {
                return AnnotationMatchingPointcut.forClassAnnotation(annotationType);
            } else if (target.supportMethod()) {
                return AnnotationMatchingPointcut.forMethodAnnotation(annotationType);
            } else {
                throw new IllegalArgumentException(
                        "annotation:"
                                + annotationType
                                + " Missing "
                                + Target.class
                                + " TYPE or METHOD information");
            }
        }
    }
}
