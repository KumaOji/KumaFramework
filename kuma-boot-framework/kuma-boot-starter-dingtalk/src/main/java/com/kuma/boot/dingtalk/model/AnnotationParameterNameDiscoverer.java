/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.core.ParameterNameDiscoverer
 */
package com.kuma.boot.dingtalk.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.springframework.core.ParameterNameDiscoverer;

public class AnnotationParameterNameDiscoverer
implements ParameterNameDiscoverer {
    public String[] getParameterNames(Method method) {
        return this.getParameterNames(method.getParameters(), method.getParameterAnnotations());
    }

    public String[] getParameterNames(Constructor<?> ctor) {
        return this.getParameterNames(ctor.getParameters(), ctor.getParameterAnnotations());
    }

    protected String[] getParameterNames(Parameter[] parameters, Annotation[][] parameterAnnotations) {
        String[] params = new String[parameterAnnotations.length];
        block0: for (int i = 0; i < parameterAnnotations.length; ++i) {
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            params[i] = parameters[i].getName();
            for (Annotation annotation : parameterAnnotation) {
                if (!com.kuma.boot.dingtalk.annatations.Parameter.class.isInstance(annotation)) continue;
                com.kuma.boot.dingtalk.annatations.Parameter dingerParam = (com.kuma.boot.dingtalk.annatations.Parameter)annotation;
                params[i] = dingerParam.value();
                continue block0;
            }
        }
        return params;
    }
}

