package com.kuma.cloud.lab.javacore.support;

import com.kuma.cloud.lab.javacore.domain.vo.HashMapBucketVO;
import com.kuma.cloud.lab.javacore.domain.vo.HashMapInspectVO;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通过反射观察 {@link HashMap} 内部 table 结构，辅助理解哈希桶与扩容。
 */
public final class HashMapStructureInspector {

    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private HashMapStructureInspector() {
    }

    public static HashMapInspectVO inspect(List<String> keys) {
        HashMap<String, String> map = new HashMap<>(keys.size(), DEFAULT_LOAD_FACTOR);
        List<HashMapBucketVO> placements = new ArrayList<>();

        for (String key : keys) {
            int hash = key.hashCode();
            int spreadHash = spread(hash);
            map.put(key, "value-" + key);
            int tableLength = tableLength(map);
            int bucketIndex = tableLength == 0 ? -1 : (tableLength - 1) & spreadHash;
            placements.add(new HashMapBucketVO(
                    key,
                    hash,
                    spreadHash,
                    bucketIndex,
                    "index = (table.length - 1) & spread(hash)"));
        }

        return new HashMapInspectVO(
                keys.size(),
                tableLength(map),
                threshold(map),
                loadFactor(map),
                placements,
                structureNotes());
    }

    public static HashMapInspectVO inspectCollisionDemo() {
        // 构造相同低位哈希的 key，便于观察链表/红黑树桶
        List<String> keys = List.of("Aa", "BB", "C#", "D$");
        return inspect(keys);
    }

    private static List<String> structureNotes() {
        return List.of(
                "JDK 8+ HashMap：数组 + 链表，链表长度 > 8 且 table.length >= 64 时转为红黑树",
                "扩容：size > threshold（capacity * loadFactor）时容量翻倍并 rehash",
                "hash 扰动：spread = (h ^ (h >>> 16))，降低高位不参与索引时的碰撞",
                "equals 与 hashCode 契约：查找时先比 hash 再比 equals");
    }

    private static int spread(int hash) {
        return hash ^ (hash >>> 16);
    }

    private static int tableLength(HashMap<?, ?> map) {
        Object[] table = tableArray(map);
        return table == null ? 0 : table.length;
    }

    private static int threshold(HashMap<?, ?> map) {
        try {
            Field field = HashMap.class.getDeclaredField("threshold");
            field.setAccessible(true);
            return field.getInt(map);
        } catch (ReflectiveOperationException ex) {
            return -1;
        }
    }

    private static float loadFactor(HashMap<?, ?> map) {
        try {
            Field field = HashMap.class.getDeclaredField("loadFactor");
            field.setAccessible(true);
            return field.getFloat(map);
        } catch (ReflectiveOperationException ex) {
            return DEFAULT_LOAD_FACTOR;
        }
    }

    private static Object[] tableArray(HashMap<?, ?> map) {
        try {
            Field field = HashMap.class.getDeclaredField("table");
            field.setAccessible(true);
            return (Object[]) field.get(map);
        } catch (ReflectiveOperationException ex) {
            return null;
        }
    }

}
