/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.http.HttpEntity
 *  org.springframework.http.HttpHeaders
 *  org.springframework.web.client.RestTemplate
 */
package com.kuma.boot.dingtalk.support;

import com.kuma.boot.dingtalk.exception.SendMsgException;

import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

public class DingerHttpTemplate
extends AbstractDingerHttpClient {
    private RestTemplate restTemplate;

    public DingerHttpTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public <T> String post(String url, Map<String, String> headers, T message) throws SendMsgException {
        HttpHeaders httpHeaders = new HttpHeaders();
        headers.forEach((headerName, headerValue) -> httpHeaders.set(headerName, headerValue));
        HttpEntity request = new HttpEntity(message, httpHeaders);
        return (String)this.restTemplate.postForObject(url, (Object)request, String.class, new Object[0]);
    }
}

