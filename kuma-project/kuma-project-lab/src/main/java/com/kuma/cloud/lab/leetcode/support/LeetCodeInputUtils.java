package com.kuma.cloud.lab.leetcode.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 解析 REST 请求中的 LeetCode 输入参数。
 */
public final class LeetCodeInputUtils {

    private LeetCodeInputUtils() {
    }

    public static int intValue(Map<String, Object> input, String key) {
        return toInt(require(input, key));
    }

    public static String stringValue(Map<String, Object> input, String key) {
        Object value = require(input, key);
        if (value instanceof String str) {
            return str;
        }
        throw illegalType(key, value);
    }

    public static int[] intArray(Map<String, Object> input, String key) {
        Object value = require(input, key);
        if (value instanceof int[] array) {
            return array;
        }
        if (value instanceof List<?> list) {
            return list.stream().mapToInt(LeetCodeInputUtils::toInt).toArray();
        }
        throw illegalType(key, value);
    }

    public static int[][] intMatrix(Map<String, Object> input, String key) {
        Object value = require(input, key);
        if (!(value instanceof List<?> rows)) {
            throw illegalType(key, value);
        }
        int[][] matrix = new int[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            Object row = rows.get(i);
            if (!(row instanceof List<?> cells)) {
                throw new IllegalArgumentException(key + "[" + i + "] 必须是数组");
            }
            matrix[i] = cells.stream().mapToInt(LeetCodeInputUtils::toInt).toArray();
        }
        return matrix;
    }

    public static char[] charArray(Map<String, Object> input, String key) {
        String value = stringValue(input, key);
        return value.toCharArray();
    }

    public static char[][] charMatrix(Map<String, Object> input, String key) {
        Object value = require(input, key);
        if (!(value instanceof List<?> rows)) {
            throw illegalType(key, value);
        }
        char[][] matrix = new char[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            Object row = rows.get(i);
            if (row instanceof String str) {
                matrix[i] = str.toCharArray();
                continue;
            }
            if (row instanceof List<?> cells) {
                char[] chars = new char[cells.size()];
                for (int j = 0; j < cells.size(); j++) {
                    chars[j] = toChar(cells.get(j));
                }
                matrix[i] = chars;
                continue;
            }
            throw new IllegalArgumentException(key + "[" + i + "] 必须是字符串或字符数组");
        }
        return matrix;
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> operationList(Map<String, Object> input, String key) {
        Object value = require(input, key);
        if (!(value instanceof List<?> list)) {
            throw illegalType(key, value);
        }
        List<Map<String, Object>> operations = new ArrayList<>();
        for (Object item : list) {
            if (!(item instanceof Map<?, ?> map)) {
                throw new IllegalArgumentException(key + " 中的元素必须是对象");
            }
            operations.add((Map<String, Object>) map);
        }
        return operations;
    }

    public static List<Integer> integerList(Map<String, Object> input, String key) {
        Object value = require(input, key);
        if (!(value instanceof List<?> list)) {
            throw illegalType(key, value);
        }
        return list.stream().map(item -> item == null ? null : toInt(item)).toList();
    }

    private static Object require(Map<String, Object> input, String key) {
        if (input == null || !input.containsKey(key)) {
            throw new IllegalArgumentException("缺少参数: " + key);
        }
        return input.get(key);
    }

    private static int toInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String str) {
            return Integer.parseInt(str);
        }
        throw illegalType("value", value);
    }

    private static char toChar(Object value) {
        if (value instanceof Character character) {
            return character;
        }
        if (value instanceof String str && str.length() == 1) {
            return str.charAt(0);
        }
        throw illegalType("char", value);
    }

    private static IllegalArgumentException illegalType(String key, Object value) {
        return new IllegalArgumentException("参数 " + key + " 类型不支持: " + (value == null ? "null" : value.getClass().getName()));
    }

}
