package com.kuma.boot.common.utils.json;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.*;

import java.util.Collection;

/**
 * json工具类, 基于hutool的进行封装,
 * 对java8的LocalDateTime时间格式进行转换, 但无法处理LocalDate, LocalTime格式, 需要使用JacksonUtil进行处理
 */
public final class HutoolJsonUtils extends JSONUtil{

    private static final JSONConfig JSON_CONFIG = JSONConfig
            .create()
            .setDateFormat(DatePattern.NORM_DATETIME_PATTERN);

    /**
     * 转换为实体
     */
    public static <T> T toBean( String json, Class<T> clazz ){
        JSONObject jsonObject = new JSONObject(json, JSON_CONFIG);
        return JSONUtil.toBean(jsonObject, clazz);
    }

    /**
     * 转换为实体
     */
    public static <T> T toBean(String json, TypeReference<T> reference){
        JSON parse = JSONUtil.parse(json, JSON_CONFIG);
        return parse.toBean(reference);
    }

    /**
     * 转换为实体
     */
    public static <T> T toBean(String json, TypeReference<T> reference, boolean ignoreError){
        JSONConfig jsonConfig = JSONConfig.create()
                .setDateFormat(DatePattern.NORM_DATETIME_PATTERN)
                .setIgnoreError(ignoreError);
        JSON parse = JSONUtil.parse(json, jsonConfig);
        return parse.toBean(reference);
    }

    /**
     * 序列化为字符串
     */
    public static  String toJsonStr(Object object){
        JSONObject jsonObject = new JSONObject(object, JSON_CONFIG);
        return JSONUtil.toJsonStr(jsonObject);
    }

    /**
     * 序列化为字符串
     */
    public static String toJsonStr(Collection<?> object){
        JSONArray jsonObject = new JSONArray(object, JSON_CONFIG);
        return JSONUtil.toJsonStr(jsonObject);
    }

    /**
     * JSON字符串转JSONObject对象
     */
    public static JSONObject parseObj(String jsonStr){
        return JSONUtil.parseObj(jsonStr, JSON_CONFIG);
    }
}
