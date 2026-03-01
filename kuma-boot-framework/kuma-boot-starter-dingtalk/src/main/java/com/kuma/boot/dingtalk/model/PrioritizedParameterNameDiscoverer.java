/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.core.ParameterNameDiscoverer
 */
package com.kuma.boot.dingtalk.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import org.springframework.core.ParameterNameDiscoverer;

public class PrioritizedParameterNameDiscoverer
implements ParameterNameDiscoverer {
    private final List<ParameterNameDiscoverer> parameterNameDiscoverers = new LinkedList<ParameterNameDiscoverer>();

    public void addDiscoverer(ParameterNameDiscoverer pnd) {
        this.parameterNameDiscoverers.add(pnd);
    }

    public String[] getParameterNames(Method method) {
        for (ParameterNameDiscoverer pnd : this.parameterNameDiscoverers) {
            String[] result = pnd.getParameterNames(method);
            if (result == null) continue;
            return result;
        }
        return null;
    }

    public String[] getParameterNames(Constructor<?> ctor) {
        for (ParameterNameDiscoverer pnd : this.parameterNameDiscoverers) {
            String[] result = pnd.getParameterNames(ctor);
            if (result == null) continue;
            return result;
        }
        return null;
    }
}

