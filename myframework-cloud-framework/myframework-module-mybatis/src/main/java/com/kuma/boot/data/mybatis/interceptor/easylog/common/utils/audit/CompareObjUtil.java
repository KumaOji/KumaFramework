/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.data.mybatis.interceptor.easylog.common.utils.audit;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * CompareObjUtil
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class CompareObjUtil {

    /**
     * 比较两个对象的不同
     */
    public static List<com.kuma.boot.data.mybatis.interceptor.easylog.common.utils.audit.Comparison> compareObj(Object beforeObj, Object afterObj ) throws Exception {
        List<com.kuma.boot.data.mybatis.interceptor.easylog.common.utils.audit.Comparison> diffs = new ArrayList<>();

        if (beforeObj == null) {
            throw new RuntimeException("原对象不能为空");
        }
        if (afterObj == null) {
            throw new RuntimeException("新对象不能为空");
        }
        if (!beforeObj.getClass().isAssignableFrom(afterObj.getClass())) {
            throw new RuntimeException("两个对象不相同，无法比较");
        }

        // 取出属性
        Field[] beforeFields = beforeObj.getClass().getDeclaredFields();
        Field[] afterFields = afterObj.getClass().getDeclaredFields();
        Field.setAccessible(beforeFields, true);
        Field.setAccessible(afterFields, true);

        // 遍历取出差异值
        if (beforeFields.length > 0) {
            for (int i = 0; i < beforeFields.length; i++) {
                Object beforeValue = beforeFields[i].get(beforeObj);
                Object afterValue = afterFields[i].get(afterObj);
                if (( beforeValue != null
                        && !"".equals(beforeValue)
                        && !beforeValue.equals(afterValue) )
                        || ( ( beforeValue == null || "".equals(beforeValue) )
                        && afterValue != null )) {
                    com.kuma.boot.data.mybatis.interceptor.easylog.common.utils.audit.Comparison comparison = new com.kuma.boot.data.mybatis.interceptor.easylog.common.utils.audit.Comparison();
                    comparison.setField(beforeFields[i].getName());
                    comparison.setBefore(beforeValue);
                    comparison.setAfter(afterValue);
                    diffs.add(comparison);
                }
            }
        }

        return diffs;
    }

    /**
     * 比较两个json串的不同
     */
    public static String campareJsonObject( String oldJsonStr, String newJsonStr1 ) {
        // 将字符串转换为json对象
        JSONObject oldJson = JSON.parseObject(oldJsonStr);
        JSONObject newJson = JSON.parseObject(newJsonStr1);
        // 递归遍历json对象所有的key-value，将其封装成path:value格式进行比较
        Map<String, Object> oldMap = new LinkedHashMap<>();
        Map<String, Object> newMap = new LinkedHashMap<>();
        convertJsonToMap(oldJson, "", oldMap);
        convertJsonToMap(newJson, "", newMap);
        Map<String, Object> differenceMap = campareMap(oldMap, newMap);
        // 将最终的比较结果把不相同的转换为json对象返回
        return convertMapToJson(differenceMap);
    }

    /**
     * 将json数据转换为map存储用于比较
     */
    private static void convertJsonToMap( Object json, String root, Map<String, Object> resultMap ) {
        if (json instanceof JSONObject jsonObject) {
            for (Object key : jsonObject.keySet()) {
                Object value = jsonObject.get(key);
                String newRoot = "".equals(root) ? key + "" : root + "." + key;
                if (value instanceof JSONObject || value instanceof JSONArray) {
                    convertJsonToMap(value, newRoot, resultMap);
                } else {
                    resultMap.put(newRoot, value);
                }
            }
        } else if (json instanceof JSONArray jsonArray) {
            for (int i = 0; i < jsonArray.size(); i++) {
                Object vaule = jsonArray.get(i);
                String newRoot = "".equals(root) ? "[" + i + "]" : root + ".[" + i + "]";
                if (vaule instanceof JSONObject || vaule instanceof JSONArray) {
                    convertJsonToMap(vaule, newRoot, resultMap);
                } else {
                    resultMap.put(newRoot, vaule);
                }
            }
        }
    }

    /**
     * 比较两个map，返回不同数据
     */
    private static Map<String, Object> campareMap(
            Map<String, Object> oldMap, Map<String, Object> newMap ) {
        // 遍历newMap，将newMap的不同数据装进oldMap，同时删除oldMap中与newMap相同的数据
        campareNewToOld(oldMap, newMap);
        // 將舊的有新的沒有的數據封裝數據結構存在舊的裡面
        campareOldToNew(oldMap);
        return oldMap;
    }

    /**
     * 將舊的有新的沒有的數據封裝數據結構存在舊的裡面
     */
    private static void campareOldToNew( Map<String, Object> oldMap ) {
        // 统一oldMap中newMap不存在的数据的数据结构，便于解析
        for (Map.Entry<String, Object> item : oldMap.entrySet()) {
            String key = item.getKey();
            Object value = item.getValue();
            int lastIndex = key.lastIndexOf(".");
            if (!( value instanceof Map )) {
                Map<String, Object> differenceMap = new HashMap<>();
                differenceMap.put("oldValue", value);
                differenceMap.put("newValue", "");
                oldMap.put(key, differenceMap);
            }
        }
    }

    /**
     * 將新的map與舊的比較，並將數據統一存在舊的裡面
     */
    private static void campareNewToOld( Map<String, Object> oldMap, Map<String, Object> newMap ) {
        for (Map.Entry<String, Object> item : newMap.entrySet()) {
            String key = item.getKey();
            Object newValue = item.getValue();
            Map<String, Object> differenceMap = new HashMap<>();
            int lastIndex = key.lastIndexOf(".");
            String lastPath = key.substring(lastIndex + 1).toLowerCase();
            if (oldMap.containsKey(key)) {
                Object oldValue = oldMap.get(key);
                if (newValue.equals(oldValue)) {
                    oldMap.remove(key);
                    continue;
                } else {
                    differenceMap.put("oldValue", oldValue);
                    differenceMap.put("newValue", newValue);
                    oldMap.put(key, differenceMap);
                }
            } else {
                differenceMap.put("oldValue", "");
                differenceMap.put("newValue", newValue);
                oldMap.put(key, differenceMap);
            }
        }
    }

    /**
     * 将已经找出不同数据的map根据key的层级结构封装成json返回
     */
    private static String convertMapToJson( Map<String, Object> map ) {
        JSONObject resultJSONObject = new JSONObject();
        for (Map.Entry<String, Object> item : map.entrySet()) {
            String key = item.getKey();
            Object value = item.getValue();
            String[] paths = key.split("\\.");
            int i = 0;
            Object remarkObject = null; // 用於深度標識對象
            int indexAll = paths.length - 1;
            while (i <= paths.length - 1) {
                String path = paths[i];
                if (i == 0) {
                    // 初始化对象标识
                    if (resultJSONObject.containsKey(path)) {
                        remarkObject = resultJSONObject.get(path);
                    } else {
                        if (indexAll > i) {
                            if (paths[i + 1].matches("\\[[0-9]+\\]")) {
                                remarkObject = new JSONArray();
                            } else {
                                remarkObject = new JSONObject();
                            }
                            resultJSONObject.put(path, remarkObject);
                        } else {
                            resultJSONObject.put(path, value);
                        }
                    }
                    i++;
                    continue;
                }
                if (path.matches("\\[[0-9]+\\]")) { // 匹配集合对象
                    int startIndex = path.lastIndexOf("[");
                    int endIndext = path.lastIndexOf("]");
                    int index = Integer.parseInt(path.substring(startIndex + 1, endIndext));
                    if (indexAll > i) {
                        if (paths[i + 1].matches("\\[[0-9]+\\]")) {
                            while (( (JSONArray) remarkObject ).size() <= index) {
                                if (( (JSONArray) remarkObject ).size() == index) {
                                    ( (JSONArray) remarkObject ).add(index, new JSONArray());
                                } else {
                                    ( (JSONArray) remarkObject ).add(null);
                                }
                            }
                        } else {
                            while (( (JSONArray) remarkObject ).size() <= index) {
                                if (( (JSONArray) remarkObject ).size() == index) {
                                    ( (JSONArray) remarkObject ).add(index, new JSONObject());
                                } else {
                                    ( (JSONArray) remarkObject ).add(null);
                                }
                            }
                        }
                        remarkObject = ( (JSONArray) remarkObject ).get(index);
                    } else {
                        while (( (JSONArray) remarkObject ).size() <= index) {
                            if (( (JSONArray) remarkObject ).size() == index) {
                                ( (JSONArray) remarkObject ).add(index, value);
                            } else {
                                ( (JSONArray) remarkObject ).add(null);
                            }
                        }
                    }
                } else {
                    if (indexAll > i) {
                        if (paths[i + 1].matches("\\[[0-9]+\\]")) {
                            if (!( (JSONObject) remarkObject ).containsKey(path)) {
                                ( (JSONObject) remarkObject ).put(path, new JSONArray());
                            }
                        } else {
                            if (!( (JSONObject) remarkObject ).containsKey(path)) {
                                ( (JSONObject) remarkObject ).put(path, new JSONObject());
                            }
                        }
                        remarkObject = ( (JSONObject) remarkObject ).get(path);
                    } else {
                        ( (JSONObject) remarkObject ).put(path, value);
                    }
                }
                i++;
            }
        }
        return JSON.toJSONString(resultJSONObject);
    }
}
