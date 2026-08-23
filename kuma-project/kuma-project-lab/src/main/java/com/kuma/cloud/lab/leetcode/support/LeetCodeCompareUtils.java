package com.kuma.cloud.lab.leetcode.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * LeetCode 结果比较与结构转换工具。
 */
public final class LeetCodeCompareUtils {

    private LeetCodeCompareUtils() {
    }

    public static boolean equals(Object expected, Object actual) {
        return deepEquals(normalize(expected), normalize(actual));
    }

    public static Object normalize(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof int[] array) {
            return Arrays.stream(array).boxed().toList();
        }
        if (value instanceof int[][] matrix) {
            return Arrays.stream(matrix).map(row -> Arrays.stream(row).boxed().toList()).toList();
        }
        if (value instanceof char[] array) {
            return new String(array);
        }
        if (value instanceof Object[] array) {
            return Arrays.stream(array).map(LeetCodeCompareUtils::normalize).toList();
        }
        if (value instanceof List<?> list) {
            return list.stream().map(LeetCodeCompareUtils::normalize).toList();
        }
        if (value instanceof Map<?, ?> map) {
            return map.entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> normalize(entry.getValue()),
                            (left, right) -> left,
                            java.util.LinkedHashMap::new
                    ));
        }
        return value;
    }

    public static List<List<Integer>> sortNestedIntegerLists(Object value) {
        List<List<Integer>> lists = new ArrayList<>();
        if (!(value instanceof List<?> outer)) {
            return lists;
        }
        for (Object item : outer) {
            if (item instanceof List<?> inner) {
                lists.add(inner.stream().map(obj -> ((Number) obj).intValue()).sorted().toList());
            }
        }
        lists.sort(Comparator.comparing(list -> list.stream().map(String::valueOf).reduce("", String::concat)));
        return lists;
    }

    private static boolean deepEquals(Object expected, Object actual) {
        if (expected instanceof List<?> expectedList && actual instanceof List<?> actualList) {
            if (expectedList.size() != actualList.size()) {
                return false;
            }
            for (int i = 0; i < expectedList.size(); i++) {
                if (!deepEquals(expectedList.get(i), actualList.get(i))) {
                    return false;
                }
            }
            return true;
        }
        if (expected instanceof Map<?, ?> expectedMap && actual instanceof Map<?, ?> actualMap) {
            if (!Objects.equals(expectedMap.keySet(), actualMap.keySet())) {
                return false;
            }
            for (Object key : expectedMap.keySet()) {
                if (!deepEquals(expectedMap.get(key), actualMap.get(key))) {
                    return false;
                }
            }
            return true;
        }
        return Objects.equals(expected, actual);
    }

}
