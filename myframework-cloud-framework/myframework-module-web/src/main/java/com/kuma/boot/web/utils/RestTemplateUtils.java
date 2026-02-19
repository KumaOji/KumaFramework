/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.http.HttpEntity
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.MediaType
 *  org.springframework.http.ResponseEntity
 *  org.springframework.http.client.ClientHttpRequestFactory
 *  org.springframework.http.client.SimpleClientHttpRequestFactory
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.client.RestTemplate
 */
package com.kuma.boot.web.utils;

import java.util.Arrays;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class RestTemplateUtils {
    public static Map httpGetRequestFactoryToMap(String url) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(1000);
        requestFactory.setReadTimeout(1000);
        RestTemplate restTemplate = new RestTemplate((ClientHttpRequestFactory)requestFactory);
        return (Map)restTemplate.getForObject(url, Map.class, new Object[0]);
    }

    public static Map restTemplateGetToMap(String url) {
        RestTemplate restTemplate = new RestTemplate();
        return (Map)restTemplate.getForObject(url, Map.class, new Object[0]);
    }

    public static String restTemplateGetToStr(String url) {
        RestTemplate restTemplate = new RestTemplate();
        return (String)restTemplate.getForObject(url, String.class, new Object[0]);
    }

    public static ResponseEntity<String> httpGetHeaders(String url) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity entity = new HttpEntity((Object)"parameters", headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class, new Object[0]);
    }

    public static String getCommonPolicyJson(String url, MultiValueMap<String, String> map, MediaType type) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(type);
        HttpEntity request = new HttpEntity(map, headers);
        ResponseEntity resp = restTemplate.postForEntity(url, (Object)request, String.class, new Object[0]);
        return (String)resp.getBody();
    }
}

