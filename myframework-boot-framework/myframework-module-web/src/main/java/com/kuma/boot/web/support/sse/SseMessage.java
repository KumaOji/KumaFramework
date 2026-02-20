/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.web.support.sse;

public class SseMessage<T> {
    private String topic = "";
    private T data;

    public SseMessage(String topic, T data) {
        this.topic = topic;
        this.data = data;
    }

    public SseMessage(T data) {
        this.data = data;
    }

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

