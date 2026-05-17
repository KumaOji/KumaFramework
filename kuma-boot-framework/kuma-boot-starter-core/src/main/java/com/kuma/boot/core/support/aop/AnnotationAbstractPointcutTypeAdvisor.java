/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.core.support.aop;

import java.lang.annotation.Annotation;
import org.springframework.aop.Pointcut;
import org.jspecify.annotations.NonNull;

/**
 * 使用{@see AnnotationPointcutType}的注解型切点处理器
 *
 * @param <A> the type parameter
 * @author livk
 * @see AnnotationAbstractPointcutAdvisor
 */
public abstract class AnnotationAbstractPointcutTypeAdvisor<A extends Annotation>
        extends AnnotationAbstractPointcutAdvisor<A> {

    @NonNull
    @Override
    public Pointcut getPointcut() {
        return autoPointcut().getPointcut(annotationType);
    }

    /**
     * <p>
     * 用于指定不同的切点类型，默认为{@link AnnotationAutoPointcut#auto()}
     * </p>
     * @return the annotation pointcut type
     */
    protected AnnotationAutoPointcut autoPointcut() {
        return AnnotationAutoPointcut.auto();
    }
}
