/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.io;

import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.utils.collection.MapUtils;
import com.kuma.boot.common.utils.common.ArgUtils;
import com.kuma.boot.common.utils.io.FileUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HttpUtils {
    public static final String GET = "GET";
    public static final String POST = "POST";

    private HttpUtils() {
    }

    public static String getRequest(String requestUrl) {
        return HttpUtils.request(requestUrl, GET);
    }

    public static String postRequest(String requestUrl) {
        return HttpUtils.request(requestUrl, POST);
    }

    public static String request(String requestUrl, String requestMethod) {
        return HttpUtils.request(requestUrl, requestMethod, "UTF-8", null);
    }

    public static String request(String requestUrl, String requestMethod, Map<String, String> headerMap) {
        return HttpUtils.request(requestUrl, requestMethod, "UTF-8", headerMap);
    }

    public static String request(String requestUrl, String requestMethod, String charset, Map<String, String> headerMap) {
        StringBuilder buffer = new StringBuilder();
        try {
            URL url = URL.of(URI.create(requestUrl), null);
            HttpURLConnection httpUrlConn = (HttpURLConnection)url.openConnection();
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            httpUrlConn.setRequestMethod(requestMethod);
            if (MapUtils.isNotEmpty(headerMap)) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    httpUrlConn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            if (GET.equalsIgnoreCase(requestMethod)) {
                httpUrlConn.connect();
            }
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charset);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            httpUrlConn.disconnect();
            return buffer.toString();
        }
        catch (Exception e) {
            throw new BootException(e);
        }
    }

    public static Map<String, String> buildHeaderMap(String filePath) {
        List<String> stringList = FileUtils.readAllLines(filePath);
        HashMap<String, String> map = new HashMap<String, String>(stringList.size());
        for (String line : stringList) {
            int index = line.indexOf(":");
            String key = line.substring(0, index).trim();
            String value = line.substring(index + 1).trim();
            map.put(key, value);
        }
        return map;
    }

    public static void download(String remoteUrl, String localUrl) {
        HttpUtils.download(remoteUrl, localUrl, null);
    }

    public static void download(String remoteUrl, String localUrl, Map<String, String> headerMap) {
        ArgUtils.notEmpty(remoteUrl, "remoteUrl");
        ArgUtils.notEmpty(localUrl, "localUrl");
        try {
            URL url = URL.of(URI.create(remoteUrl), null);
            URLConnection conn = url.openConnection();
            if (MapUtils.isNotEmpty(headerMap)) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    conn.setRequestProperty(key, value);
                }
            }
            try (DataInputStream dataInputStream = new DataInputStream(conn.getInputStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(new File(localUrl));){
                int length;
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                while ((length = dataInputStream.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
                fileOutputStream.write(output.toByteArray());
            }
        }
        catch (IOException e) {
            throw new BootException(e);
        }
    }

    public static Object getRequest(String requestUrl, String charSetName) {
        String res = "";
        StringBuilder buffer = new StringBuilder();
        try {
            URL url = URL.of(URI.create(requestUrl), null);
            HttpURLConnection urlCon = (HttpURLConnection)url.openConnection();
            if (200 == urlCon.getResponseCode()) {
                InputStream is = urlCon.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, charSetName);
                BufferedReader br = new BufferedReader(isr);
                String str = null;
                while ((str = br.readLine()) != null) {
                    buffer.append(str);
                }
                br.close();
                isr.close();
                is.close();
                res = buffer.toString();
                return res;
            }
            throw new Exception("\u8fde\u63a5\u5931\u8d25");
        }
        catch (Exception e) {
            LogUtils.error(e);
            return null;
        }
    }

    public static Object postRequest(String path, String post) {
        URL url = null;
        try {
            int len;
            url = URL.of(URI.create(path), null);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod(POST);
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(2000);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            printWriter.write(post);
            printWriter.flush();
            BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] arr = new byte[1024];
            while ((len = bis.read(arr)) != -1) {
                bos.write(arr, 0, len);
                bos.flush();
            }
            bos.close();
            return bos.toString("utf-8");
        }
        catch (Exception e) {
            LogUtils.error(e);
            return null;
        }
    }
}

