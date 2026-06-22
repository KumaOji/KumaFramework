package com.kuma.boot.data.vector.support.memory;

import com.kuma.boot.data.vector.core.VectorStore;
import com.kuma.boot.data.vector.core.model.Distance;
import com.kuma.boot.data.vector.core.model.VectorDocument;
import com.kuma.boot.data.vector.core.model.VectorMatch;
import com.kuma.boot.data.vector.core.model.VectorSearchRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 纯内存向量库实现（暴力余弦/点积检索）。
 *
 * <p>零外部依赖，开箱即用，适合单元测试、本地开发与小规模数据场景。数据仅存于进程内存，
 * 应用重启即丢失，不适用于生产大规模检索。
 */
public class InMemoryVectorStore implements VectorStore {

    /** collection -> (id -> document) */
    private final Map<String, Map<String, VectorDocument>> store = new ConcurrentHashMap<>();
    /** collection -> 度量方式 */
    private final Map<String, Distance> distances = new ConcurrentHashMap<>();

    @Override
    public void createCollection(String collection, int dimension, Distance distance) {
        store.computeIfAbsent(collection, k -> new ConcurrentHashMap<>());
        distances.put(collection, distance == null ? Distance.COSINE : distance);
    }

    @Override
    public boolean collectionExists(String collection) {
        return store.containsKey(collection);
    }

    @Override
    public void deleteCollection(String collection) {
        store.remove(collection);
        distances.remove(collection);
    }

    @Override
    public void upsert(String collection, List<VectorDocument> documents) {
        Map<String, VectorDocument> col = store.computeIfAbsent(collection, k -> new ConcurrentHashMap<>());
        for (VectorDocument doc : documents) {
            String id = (doc.id() == null || doc.id().isBlank()) ? UUID.randomUUID().toString() : doc.id();
            col.put(id, new VectorDocument(id, doc.vector(), doc.content(), doc.metadata()));
        }
    }

    @Override
    public List<VectorMatch> search(String collection, VectorSearchRequest request) {
        Map<String, VectorDocument> col = store.get(collection);
        if (col == null || col.isEmpty() || request.getQueryVector() == null) {
            return List.of();
        }
        Distance distance = distances.getOrDefault(collection, Distance.COSINE);
        float[] query = request.getQueryVector();

        List<VectorMatch> matches = new ArrayList<>();
        for (VectorDocument doc : col.values()) {
            if (!matchesFilter(doc.metadata(), request.getFilter())) continue;
            if (doc.vector() == null || doc.vector().length != query.length) continue;
            double score = score(query, doc.vector(), distance);
            if (score < request.getMinScore()) continue;
            matches.add(new VectorMatch(doc.id(), score, doc.content(), doc.metadata()));
        }
        matches.sort(Comparator.comparingDouble(VectorMatch::score).reversed());
        int topK = Math.max(request.getTopK(), 0);
        return (topK == 0 || matches.size() <= topK) ? matches : matches.subList(0, topK);
    }

    @Override
    public void delete(String collection, List<String> ids) {
        Map<String, VectorDocument> col = store.get(collection);
        if (col != null) ids.forEach(col::remove);
    }

    @Override
    public void deleteByFilter(String collection, Map<String, Object> filter) {
        Map<String, VectorDocument> col = store.get(collection);
        if (col == null) return;
        col.values().removeIf(doc -> matchesFilter(doc.metadata(), filter));
    }

    @Override
    public long count(String collection) {
        Map<String, VectorDocument> col = store.get(collection);
        return col == null ? 0 : col.size();
    }

    @Override
    public String provider() {
        return "memory";
    }

    // ── 内部工具 ─────────────────────────────────────────────────────────────

    /** filter 为空视为匹配；否则要求 metadata 中每个 key 的值都等值匹配 */
    private static boolean matchesFilter(Map<String, Object> metadata, Map<String, Object> filter) {
        if (filter == null || filter.isEmpty()) return true;
        for (Map.Entry<String, Object> e : filter.entrySet()) {
            Object actual = metadata.get(e.getKey());
            if (actual == null || !actual.equals(e.getValue())) return false;
        }
        return true;
    }

    /** 统一归一化为「越大越相似」的 score */
    private static double score(float[] a, float[] b, Distance distance) {
        return switch (distance) {
            case DOT -> dot(a, b);
            case EUCLID -> 1.0 / (1.0 + Math.sqrt(squaredDistance(a, b)));
            case COSINE -> cosine(a, b);
        };
    }

    private static double dot(float[] a, float[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) sum += (double) a[i] * b[i];
        return sum;
    }

    private static double squaredDistance(float[] a, float[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double d = (double) a[i] - b[i];
            sum += d * d;
        }
        return sum;
    }

    private static double cosine(float[] a, float[] b) {
        double dot = 0, na = 0, nb = 0;
        for (int i = 0; i < a.length; i++) {
            dot += (double) a[i] * b[i];
            na += (double) a[i] * a[i];
            nb += (double) b[i] * b[i];
        }
        if (na == 0 || nb == 0) return 0;
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }
}
