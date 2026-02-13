/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.model;

public class Singleton {
    private Singleton() {
    }

    public static Singleton getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        public static final Singleton INSTANCE = new Singleton();

        private SingletonHolder() {
        }
    }
}

