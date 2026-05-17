package com.kuma.boot.common.chain.core.context;

import java.util.HashMap;
import java.util.Map;

/**
 * 责任链上下文类，用于在处理者之间传递数据
 * @param <P> Param类型，表示请求参数
 * @param <R> Response类型，表示响应结果
 */
public class HandlerContext<P, R> {
    private P request;
    private R response;
    private Map<String, Object> attributes = new HashMap<>();

    public HandlerContext() {
    }

    public HandlerContext(P request, R response) {
        this.request = request;
        this.response = response;
    }

    public HandlerContext(P request, R response, Map<String, Object> attributes) {
        this.request = request;
        this.response = response;
        this.attributes = attributes;
    }

    @SuppressWarnings("unchecked")
    public <V> V getAttribute(String key, Class<V> type) {
        return (V) attributes.get(key);
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    public P getRequest() { return request; }
    public void setRequest(P request) { this.request = request; }

    public R getResponse() { return response; }
    public void setResponse(R response) { this.response = response; }

    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
}
