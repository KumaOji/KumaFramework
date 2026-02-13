/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.reflect.TypeToken
 */
package com.kuma.boot.common.utils.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

public final class GsonUtils {
    private static final Gson GSON = GsonUtils.getInstance();

    public static Gson getInstance() {
        return JacksonHolder.INSTANCE;
    }

    private GsonUtils() {
    }

    public static String gsonToString(Object object) {
        String gsonString = null;
        if (GSON != null) {
            gsonString = GSON.toJson(object);
        }
        return gsonString;
    }

    public static <T> T gsonToBean(String gsonString, Class<T> cls) {
        Object t = null;
        if (GSON != null) {
            t = GSON.fromJson(gsonString, cls);
        }
        return (T)t;
    }

    public static <T> List<T> gsonToList(String gsonString, Class<T> cls) {
        List list = null;
        if (GSON != null) {
            list = (List)GSON.fromJson(gsonString, TypeToken.getParameterized(List.class, (Type[])new Type[]{cls}).getType());
        }
        return list;
    }

    public static <T> List<Map<String, T>> gsonToListMaps(String gsonString) {
        List list = null;
        if (GSON != null) {
            list = (List)GSON.fromJson(gsonString, new TypeToken<List<Map<String, T>>>(){}.getType());
        }
        return list;
    }

    public static <T> Map<String, T> gsonToMaps(String gsonString) {
        Map map = null;
        if (GSON != null) {
            map = (Map)GSON.fromJson(gsonString, new TypeToken<Map<String, T>>(){}.getType());
        }
        return map;
    }

    public static String beanToJson(Object object) {
        return GSON.toJson(object);
    }

    private static class JacksonHolder {
        private static final Gson INSTANCE = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, (json, type, jsonDeserializationContext) -> {
            String datetime = json.getAsJsonPrimitive().getAsString();
            return LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }).registerTypeAdapter(LocalDate.class, (json, type, jsonDeserializationContext) -> {
            String datetime = json.getAsJsonPrimitive().getAsString();
            return LocalDate.parse(datetime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }).registerTypeAdapter(Date.class, (json, type, jsonDeserializationContext) -> {
            String datetime = json.getAsJsonPrimitive().getAsString();
            LocalDateTime localDateTime = LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        }).registerTypeAdapter(LocalDateTime.class, (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))).registerTypeAdapter(LocalDate.class, (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))).registerTypeAdapter(Date.class, (src, typeOfSrc, context) -> {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(src.toInstant(), ZoneId.systemDefault());
            return new JsonPrimitive(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }).create();

        private JacksonHolder() {
        }
    }
}

