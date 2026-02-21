/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.collection.CollUtil
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  org.apache.hc.client5.http.classic.methods.HttpGet
 *  org.apache.hc.client5.http.classic.methods.HttpPost
 *  org.apache.hc.client5.http.entity.UrlEncodedFormEntity
 *  org.apache.hc.client5.http.impl.classic.CloseableHttpClient
 *  org.apache.hc.client5.http.impl.classic.CloseableHttpResponse
 *  org.apache.hc.client5.http.impl.classic.HttpClients
 *  org.apache.hc.core5.http.ClassicHttpRequest
 *  org.apache.hc.core5.http.HttpEntity
 *  org.apache.hc.core5.http.io.entity.EntityUtils
 *  org.apache.hc.core5.http.io.entity.StringEntity
 *  org.apache.hc.core5.http.message.BasicNameValuePair
 */
package com.kuma.boot.ip2region.http;

import cn.hutool.core.collection.CollUtil;
import com.kuma.boot.common.utils.lang.StringUtils;
import java.lang.invoke.CallSite;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;

public class SmartHttpUtil {
    public static String sendGet(String url, Map<String, String> params, Map<String, String> header) throws Exception {
        HttpGet httpGet = null;
        String body = "";
        try {
            CloseableHttpResponse response;
            int n;
            CloseableHttpClient httpClient = HttpClients.createDefault();
            ArrayList<CallSite> mapList = new ArrayList<CallSite>();
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    mapList.add((CallSite)((Object)(entry.getKey() + "=" + entry.getValue())));
                }
            }
            if (CollUtil.isNotEmpty(mapList)) {
                url = (String)url + "?";
                String paramsStr = StringUtils.join(mapList, (String)"&");
                url = (String)url + (String)paramsStr;
            }
            httpGet = new HttpGet((String)url);
            httpGet.setHeader("Content-type", (Object)"application/json; charset=utf-8");
            httpGet.setHeader("User-Agent", (Object)"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            if (header != null) {
                for (Map.Entry entry : header.entrySet()) {
                    httpGet.setHeader((String)entry.getKey(), entry.getValue());
                }
            }
            if ((n = (response = httpClient.execute((ClassicHttpRequest)httpGet)).getCode()) != 200) {
                throw new RuntimeException("\u8bf7\u6c42\u5931\u8d25");
            }
            body = EntityUtils.toString(null, (String)"UTF-8");
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (httpGet != null) {
                // empty if block
            }
        }
        return body;
    }

    public static String sendPostJson(String url, String json, Map<String, String> header) throws Exception {
        HttpPost httpPost = null;
        String body = "";
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", (Object)"application/json; charset=utf-8");
            httpPost.setHeader("User-Agent", (Object)"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            if (header != null) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    httpPost.setHeader(entry.getKey(), (Object)entry.getValue());
                }
            }
            StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
            httpPost.setEntity((HttpEntity)entity);
            CloseableHttpResponse response = httpClient.execute((ClassicHttpRequest)httpPost);
            int statusCode = response.getCode();
            if (statusCode != 200) {
                throw new RuntimeException("\u8bf7\u6c42\u5931\u8d25");
            }
            body = EntityUtils.toString(null, (String)"UTF-8");
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (httpPost != null) {
                // empty if block
            }
        }
        return body;
    }

    public static String sendPostForm(String url, Map<String, String> params, Map<String, String> header) throws Exception {
        HttpPost httpPost = null;
        String body = "";
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", (Object)"application/x-www-form-urlencoded");
            httpPost.setHeader("User-Agent", (Object)"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            if (header != null) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    httpPost.setHeader(entry.getKey(), (Object)entry.getValue());
                }
            }
            ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
            httpPost.setEntity((HttpEntity)new UrlEncodedFormEntity(nvps));
            CloseableHttpResponse closeableHttpResponse = httpClient.execute((ClassicHttpRequest)httpPost);
            int n = closeableHttpResponse.getCode();
            if (n != 200) {
                throw new RuntimeException("\u8bf7\u6c42\u5931\u8d25");
            }
            body = EntityUtils.toString(null, (String)"UTF-8");
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (httpPost != null) {
                // empty if block
            }
        }
        return body;
    }
}

