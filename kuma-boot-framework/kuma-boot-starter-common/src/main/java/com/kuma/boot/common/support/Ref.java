package com.kuma.boot.common.support;

public class Ref<T> {

    private volatile T data;

    public Ref(T data) {
        this.data = data;
    }

    public boolean isNull() {
        return data == null;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
