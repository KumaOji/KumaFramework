/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.alibaba.fastjson2.JSON
 *  com.jayway.jsonpath.DocumentContext
 *  com.jayway.jsonpath.JsonPath
 *  com.jayway.jsonpath.PathNotFoundException
 *  com.jayway.jsonpath.Predicate
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.web.request.altas.comparator;

import com.alibaba.fastjson2.JSON;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.Predicate;
import com.kuma.boot.common.utils.log.LogUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JsonPathValueExtractor {
    private final boolean enabled;

    public JsonPathValueExtractor(boolean enabled) {
        this.enabled = enabled;
    }

    public Object extractValue(Object obj, String jsonPath) {
        if (!this.enabled || obj == null || jsonPath == null || jsonPath.trim().isEmpty()) {
            return null;
        }
        try {
            String json = JSON.toJSONString((Object)obj);
            DocumentContext context = JsonPath.parse((String)json);
            return context.read(jsonPath, new Predicate[0]);
        }
        catch (PathNotFoundException e) {
            LogUtils.debug((String)"Path not found: {} in object: {}", (Object[])new Object[]{jsonPath, obj.getClass().getSimpleName()});
            return null;
        }
        catch (Exception e) {
            LogUtils.debug((String)"Failed to extract value from path: {} in object: {}", (Object[])new Object[]{jsonPath, obj.getClass().getSimpleName(), e});
            return null;
        }
    }

    public Map<String, Object> extractValues(Object obj, List<String> jsonPaths) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        if (!this.enabled || obj == null || jsonPaths == null || jsonPaths.isEmpty()) {
            return result;
        }
        try {
            String json = JSON.toJSONString((Object)obj);
            DocumentContext context = JsonPath.parse((String)json);
            for (String jsonPath : jsonPaths) {
                if (jsonPath == null || jsonPath.trim().isEmpty()) continue;
                try {
                    Object value = context.read(jsonPath, new Predicate[0]);
                    result.put(jsonPath, value);
                }
                catch (PathNotFoundException e) {
                    LogUtils.debug((String)"Path not found: {}", (Object[])new Object[]{jsonPath});
                    result.put(jsonPath, null);
                }
                catch (Exception e) {
                    LogUtils.debug((String)"Failed to extract value from path: {}", (Object[])new Object[]{jsonPath, e});
                    result.put(jsonPath, null);
                }
            }
        }
        catch (Exception e) {
            LogUtils.debug((String)"Failed to serialize object for value extraction: {}", (Object[])new Object[]{obj.getClass().getSimpleName(), e});
        }
        return result;
    }

    public ValueComparisonResult compareValues(Object obj1, Object obj2, String jsonPath) {
        Object value1 = this.extractValue(obj1, jsonPath);
        Object value2 = this.extractValue(obj2, jsonPath);
        return new ValueComparisonResult(jsonPath, value1, value2, Objects.equals(value1, value2));
    }

    public List<ValueComparisonResult> compareValues(Object obj1, Object obj2, List<String> jsonPaths) {
        ArrayList<ValueComparisonResult> results = new ArrayList<ValueComparisonResult>();
        if (!this.enabled || jsonPaths == null || jsonPaths.isEmpty()) {
            return results;
        }
        Map<String, Object> values1 = this.extractValues(obj1, jsonPaths);
        Map<String, Object> values2 = this.extractValues(obj2, jsonPaths);
        for (String jsonPath : jsonPaths) {
            Object value1 = values1.get(jsonPath);
            Object value2 = values2.get(jsonPath);
            boolean isEqual = Objects.equals(value1, value2);
            results.add(new ValueComparisonResult(jsonPath, value1, value2, isEqual));
        }
        return results;
    }

    public boolean hasPath(Object obj, String jsonPath) {
        if (!this.enabled || obj == null || jsonPath == null || jsonPath.trim().isEmpty()) {
            return false;
        }
        try {
            String json = JSON.toJSONString((Object)obj);
            DocumentContext context = JsonPath.parse((String)json);
            context.read(jsonPath, new Predicate[0]);
            return true;
        }
        catch (PathNotFoundException e) {
            return false;
        }
        catch (Exception e) {
            LogUtils.debug((String)"Error checking path existence: {}", (Object[])new Object[]{jsonPath, e});
            return false;
        }
    }

    public List<String> getAvailablePaths(Object obj) {
        ArrayList<String> paths = new ArrayList<String>();
        if (!this.enabled || obj == null) {
            return paths;
        }
        try {
            String json = JSON.toJSONString((Object)obj);
            DocumentContext context = JsonPath.parse((String)json);
            paths.addAll(this.discoverPaths(context, "$"));
        }
        catch (Exception e) {
            LogUtils.debug((String)"Failed to discover paths in object: {}", (Object[])new Object[]{obj.getClass().getSimpleName(), e});
        }
        return paths;
    }

    private List<String> discoverPaths(DocumentContext context, String basePath) {
        ArrayList<String> paths = new ArrayList<String>();
        try {
            paths.add(basePath);
        }
        catch (Exception e) {
            LogUtils.debug((String)"Failed to discover paths from base: {}", (Object[])new Object[]{basePath, e});
        }
        return paths;
    }

    public static boolean isJsonPathAvailable() {
        try {
            Class.forName("com.jayway.jsonpath.JsonPath");
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static class ValueComparisonResult {
        private final String jsonPath;
        private final Object value1;
        private final Object value2;
        private final boolean isEqual;

        public ValueComparisonResult(String jsonPath, Object value1, Object value2, boolean isEqual) {
            this.jsonPath = jsonPath;
            this.value1 = value1;
            this.value2 = value2;
            this.isEqual = isEqual;
        }

        public String getJsonPath() {
            return this.jsonPath;
        }

        public Object getValue1() {
            return this.value1;
        }

        public Object getValue2() {
            return this.value2;
        }

        public boolean isEqual() {
            return this.isEqual;
        }

        public String toString() {
            return String.format("ValueComparisonResult{path='%s', value1=%s, value2=%s, equal=%s}", this.jsonPath, this.value1, this.value2, this.isEqual);
        }
    }
}

