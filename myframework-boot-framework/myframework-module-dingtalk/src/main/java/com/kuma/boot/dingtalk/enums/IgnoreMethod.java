/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.enums;

import com.kuma.boot.dingtalk.model.DingerInvocationHandler;

public enum IgnoreMethod {
    EQUALS("equals"){

        @Override
        public Boolean execute(DingerInvocationHandler invocationHandler, Object[] args) {
            return invocationHandler.equals(args[0]);
        }
    }
    ,
    CLONE("clone"){

        @Override
        public Object execute(DingerInvocationHandler invocationHandler, Object[] args) throws CloneNotSupportedException {
            return invocationHandler.clone();
        }
    }
    ,
    HASH_CODE("hashCode"){

        @Override
        public Integer execute(DingerInvocationHandler invocationHandler, Object[] args) throws Exception {
            return invocationHandler.hashCode();
        }
    }
    ,
    TO_STRING("toString"){

        @Override
        public String execute(DingerInvocationHandler invocationHandler, Object[] args) throws Exception {
            return invocationHandler.toString();
        }
    };

    private String methodName;

    private IgnoreMethod(String methodName) {
        this.methodName = methodName;
    }

    public abstract Object execute(DingerInvocationHandler var1, Object[] var2) throws Exception;

    public String getMethodName() {
        return this.methodName;
    }
}

