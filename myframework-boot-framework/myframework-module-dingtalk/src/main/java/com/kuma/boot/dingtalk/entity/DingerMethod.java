/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

public class DingerMethod {
    String methodName;
    String[] methodParams;
    int[] paramTypes;

    public DingerMethod(String methodName, String[] methodParams, int[] paramTypes) {
        this.methodName = methodName;
        this.methodParams = methodParams;
        this.paramTypes = paramTypes;
    }

    public boolean check() {
        if (this.paramTypes == null) {
            return false;
        }
        int length = this.methodParams.length;
        for (int index : this.paramTypes) {
            if (index < length) continue;
            return true;
        }
        return false;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public String[] getMethodParams() {
        return this.methodParams;
    }

    public int[] getParamTypes() {
        return this.paramTypes;
    }
}

