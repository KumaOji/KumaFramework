/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.lang.TypeReference
 *  cn.hutool.json.JSON
 *  cn.hutool.json.JSONArray
 *  cn.hutool.json.JSONConfig
 *  cn.hutool.json.JSONObject
 *  cn.hutool.json.JSONUtil
 */
package com.kuma.boot.common.utils.json;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import java.util.Collection;

public final class HutoolJsonUtils
extends JSONUtil {
    private static final JSONConfig JSON_CONFIG = JSONConfig.create().setDateFormat("yyyy-MM-dd HH:mm:ss");

    public static <T> T toBean(String json, Class<T> clazz) {
        JSONObject jsonObject = new JSONObject((Object)json, JSON_CONFIG);
        return (T)JSONUtil.toBean((JSONObject)jsonObject, clazz);
    }

    public static <T> T toBean(String json, TypeReference<T> reference) {
        JSON parse = JSONUtil.parse((Object)json, (JSONConfig)JSON_CONFIG);
        return (T)parse.toBean(reference);
    }

    public static <T> T toBean(String json, TypeReference<T> reference, boolean ignoreError) {
        JSONConfig jsonConfig = JSONConfig.create().setDateFormat("yyyy-MM-dd HH:mm:ss").setIgnoreError(ignoreError);
        JSON parse = JSONUtil.parse((Object)json, (JSONConfig)jsonConfig);
        return (T)parse.toBean(reference);
    }

    public static String toJsonStr(Object object) {
        JSONObject jsonObject = new JSONObject(object, JSON_CONFIG);
        return JSONUtil.toJsonStr((JSON)jsonObject);
    }

    public static String toJsonStr(Collection<?> object) {
        JSONArray jsonObject = new JSONArray(object, JSON_CONFIG);
        return JSONUtil.toJsonStr((JSON)jsonObject);
    }

    public static JSONObject parseObj(String jsonStr) {
        return JSONUtil.parseObj((Object)jsonStr, (JSONConfig)JSON_CONFIG);
    }
}

