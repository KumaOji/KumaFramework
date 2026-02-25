/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.ratelimit.ratelimitsnowjean.commoon.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {
    private HttpURLConnection connection;
    private Charset charset = Charset.forName("UTF-8");
    private int readTimeout = 32000;
    private int connectTimeout = 10000;
    private String method = "GET";
    private boolean doInput = true;
    private Map<String, String> headers = new HashMap<String, String>();
    private Map<String, String> data = new HashMap<String, String>();

    public static HttpUtil connect(String url) throws IOException {
        return new HttpUtil((HttpURLConnection)new URL(url).openConnection());
    }

    private HttpUtil() {
    }

    private HttpUtil(HttpURLConnection connection) {
        this.connection = connection;
    }

    public HttpUtil setReadTimeout(int timeout) {
        this.readTimeout = timeout;
        return this;
    }

    public HttpUtil setConnectTimeout(int timeout) {
        this.connectTimeout = timeout;
        return this;
    }

    public HttpUtil setMethod(String method) {
        this.method = method;
        return this;
    }

    public HttpUtil setHeaders(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public HttpUtil setDoInput(boolean is) {
        this.doInput = is;
        return this;
    }

    public HttpUtil setCharset(String charset) {
        this.charset = Charset.forName(charset);
        return this;
    }

    public HttpUtil setData(String key, String value) {
        this.data.put(key, value);
        return this;
    }

    public HttpUtil execute() throws IOException {
        this.headers.put("Connection", "close");
        for (String key : this.headers.keySet()) {
            this.connection.setRequestProperty(key, this.headers.get(key));
        }
        this.connection.setReadTimeout(this.readTimeout);
        this.connection.setConnectTimeout(this.connectTimeout);
        this.connection.setRequestMethod(this.method.toUpperCase());
        this.connection.setDoInput(this.doInput);
        if (!this.data.isEmpty() & !this.method.equalsIgnoreCase("GET")) {
            this.connection.setDoOutput(true);
            OutputStream output = this.connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, this.charset));
            writer.write(this.getDataString());
            writer.flush();
            writer.close();
        }
        this.connection.connect();
        return this;
    }

    private String getDataString() {
        StringBuilder builder = new StringBuilder();
        ArrayList<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(this.data.entrySet());
        for (int i = 0; i < list.size(); ++i) {
            Map.Entry entry = (Map.Entry)list.get(i);
            builder.append((String)entry.getKey()).append("=").append((String)entry.getValue());
            if (i >= list.size() - 1) continue;
            builder.append("&");
        }
        return builder.toString();
    }

    public HttpURLConnection getConnection() {
        return this.connection;
    }

    public String getBody(String ... charsets) {
        String charset = "UTF-8";
        if (charsets.length > 0) {
            charset = charsets[0];
        }
        try {
            InputStream inputStream = this.connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charset));
            String line = bufferedReader.readLine();
            StringBuilder builder = new StringBuilder();
            while (line != null) {
                builder.append(line);
                line = bufferedReader.readLine();
            }
            return builder.toString();
        }
        catch (IOException iOException) {
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        String body = HttpUtil.connect("http://www.baidu.com").setMethod("GET").execute().getBody(new String[0]);
        System.out.println(body);
    }
}

