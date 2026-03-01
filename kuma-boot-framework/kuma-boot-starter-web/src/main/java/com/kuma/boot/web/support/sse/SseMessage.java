package com.kuma.boot.web.support.sse;


/**
 * sse消息
 *
 * @author zhanghongbin
 */
public class SseMessage<T> {

    // 主题：不同位置推送的内容不同
    private String topic = "";

    // 推送消息
    private T data;

    public SseMessage(String topic, T data) {
        this.topic = topic;
        this.data = data;
    }

    public SseMessage(T data) {
        this.data = data;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
