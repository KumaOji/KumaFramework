/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.secure;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.secure.MD5Utils;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class SignUtils {
    public static <T> String sign(String accessSecret, String url) throws IllegalAccessException {
        HashMap<String, Object> signMap = new HashMap<String, Object>();
        Map<String, Object> paramMap = SignUtils.getUrlParams(url);
        if (paramMap != null) {
            signMap.putAll(paramMap);
        }
        StringBuffer sb = new StringBuffer();
        SignUtils.sortMapByKey(signMap).forEach((k, v) -> sb.append((String)k).append("=").append(v).append("&"));
        sb.deleteCharAt(sb.length() - 1).append(accessSecret);
        return url + "&sig=" + MD5Utils.md5(sb.toString());
    }

    public static Map<String, String> sortMapByValue(Map<String, String> oriMap) {
        if (oriMap == null || oriMap.isEmpty()) {
            return null;
        }
        LinkedHashMap<String, String> sortedMap = new LinkedHashMap<String, String>();
        ArrayList<Map.Entry<String, String>> entryList = new ArrayList<Map.Entry<String, String>>(oriMap.entrySet());
        Collections.sort(entryList, new MapValueComparator());
        Iterator iter = entryList.iterator();
        Map.Entry tmpEntry = null;
        while (iter.hasNext()) {
            tmpEntry = (Map.Entry)iter.next();
            sortedMap.put((String)tmpEntry.getKey(), (String)tmpEntry.getValue());
        }
        return sortedMap;
    }

    public static Map<String, Object> urlParams2Map(String param) {
        String[] params;
        HashMap<String, Object> map = new HashMap<String, Object>();
        if ("".equals(param) || null == param) {
            return map;
        }
        for (String s : params = param.split("&")) {
            String[] p = s.split("=");
            if (p.length != 2) continue;
            map.put(p[0], p[1]);
        }
        return map;
    }

    public static String map2UrlParams(Map<String, String> map, boolean isSort) {
        if (map == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        ArrayList<String> keys = new ArrayList<String>(map.keySet());
        if (isSort) {
            Collections.sort(keys);
        }
        for (String key : keys) {
            String value = map.get(key);
            sb.append(key).append("=").append(value);
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0, s.lastIndexOf("&"));
        }
        return s;
    }

    public static Map<String, Object> sortMapByKey(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        TreeMap<String, Object> sortMap = new TreeMap<String, Object>(new MapKeyComparator());
        sortMap.putAll(map);
        return sortMap;
    }

    public static <T> String sign(String accessSecret, String url, Map<String, Object> headers, T body) throws IllegalAccessException {
        Map<String, Object> bodyMap;
        Map<String, Object> paramMap;
        HashMap<String, Object> signMap = new HashMap<String, Object>();
        if (headers != null) {
            signMap.putAll(headers);
        }
        if ((paramMap = SignUtils.getUrlParams(url)) != null) {
            signMap.putAll(paramMap);
        }
        if ((bodyMap = SignUtils.getBodyParams(body)) != null) {
            signMap.putAll(bodyMap);
        }
        StringBuffer sb = new StringBuffer();
        signMap.forEach((k, v) -> sb.append((String)k).append("=").append(v).append("&"));
        sb.append("accessSecret=").append(accessSecret);
        return SignUtils.stringToMD5(sb.toString());
    }

    private static Map<String, Object> getUrlParams(String url) {
        if (StringUtils.isBlank(url) || !url.contains("?")) {
            return null;
        }
        HashMap<String, Object> paramMap = new HashMap<String, Object>();
        String params = url.split("\\?")[1];
        for (String param : params.split("&")) {
            String[] p = param.split("=");
            paramMap.put(p[0], p[1]);
        }
        return paramMap;
    }

    private static <T> Map<String, Object> getBodyParams(T body) throws IllegalAccessException {
        if (body == null) {
            return null;
        }
        HashMap<String, Object> bodyMap = new HashMap<String, Object>();
        for (Field field : body.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            bodyMap.put(field.getName(), field.get(body));
        }
        return bodyMap;
    }

    private static String stringToMD5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(plainText.getBytes());
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("\u6ca1\u6709\u8fd9\u4e2amd5\u7b97\u6cd5\uff01");
        }
        return new BigInteger(1, secretBytes).toString(16);
    }

    static class MapValueComparator
    implements Comparator<Map.Entry<String, String>> {
        MapValueComparator() {
        }

        @Override
        public int compare(Map.Entry<String, String> me1, Map.Entry<String, String> me2) {
            return me1.getValue().compareTo(me2.getValue());
        }
    }

    static class MapKeyComparator
    implements Comparator<String> {
        MapKeyComparator() {
        }

        @Override
        public int compare(String str1, String str2) {
            return str1.compareTo(str2);
        }
    }
}

