/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.model;

public class DingerParameterNameDiscoverer
extends PrioritizedParameterNameDiscoverer {
    public DingerParameterNameDiscoverer() {
        this.addDiscoverer(new AnnotationParameterNameDiscoverer());
    }
}

