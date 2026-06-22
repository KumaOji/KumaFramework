package com.kuma.boot.data.vector.support.qdrant;

import com.kuma.boot.data.vector.core.VectorStore;
import com.kuma.boot.data.vector.core.model.Distance;
import com.kuma.boot.data.vector.core.model.VectorDocument;
import com.kuma.boot.data.vector.core.model.VectorMatch;
import com.kuma.boot.data.vector.core.model.VectorSearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 基于 Qdrant REST API 的 {@link VectorStore} 实现。
 *
 * <p>通过 Spring {@link RestClient} 直连 Qdrant HTTP 端口（默认 6333），不引入额外客户端依赖。
 * 文本内容写入 payload 的 {@code content} 字段，业务元数据平铺写入 payload，检索时一并返回。
 *
 * @see <a href="https://qdrant.tech/documentation/concepts/">Qdrant 文档</a>
 */
public class QdrantVectorStore implements VectorStore {

    private static final Logger log = LoggerFactory.getLogger(QdrantVectorStore.class);

    /** payload 中存放原始文本的字段名 */
    private static final String CONTENT_KEY = "content";

    private final RestClient client;

    public QdrantVectorStore(String host, int httpPort, boolean useHttps, String apiKey) {
        String baseUrl = (useHttps ? "https://" : "http://") + host + ":" + httpPort;
        RestClient.Builder builder = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(new SimpleClientHttpRequestFactory());
        if (apiKey != null && !apiKey.isBlank()) {
            builder.defaultHeader("api-key", apiKey);
        }
        this.client = builder.build();
    }

    @Override
    public void createCollection(String collection, int dimension, Distance distance) {
        if (collectionExists(collection)) return;
        client.put()
                .uri("/collections/{c}", collection)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("vectors", Map.of("size", dimension, "distance", mapDistance(distance))))
                .retrieve()
                .toBodilessEntity();
        log.info("Qdrant collection '{}' created (dimension={}, distance={})", collection, dimension, distance);
    }

    @Override
    public boolean collectionExists(String collection) {
        try {
            client.get().uri("/collections/{c}", collection).retrieve().toBodilessEntity();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void deleteCollection(String collection) {
        try {
            client.delete().uri("/collections/{c}", collection).retrieve().toBodilessEntity();
        } catch (Exception e) {
            log.debug("Qdrant delete collection '{}': {}", collection, e.getMessage());
        }
    }

    @Override
    public void upsert(String collection, List<VectorDocument> documents) {
        if (documents == null || documents.isEmpty()) return;
        List<Map<String, Object>> points = new ArrayList<>(documents.size());
        for (VectorDocument doc : documents) {
            String id = (doc.id() == null || doc.id().isBlank()) ? UUID.randomUUID().toString() : doc.id();
            Map<String, Object> payload = new HashMap<>(doc.metadata());
            if (doc.content() != null) payload.put(CONTENT_KEY, doc.content());
            Map<String, Object> point = new HashMap<>();
            point.put("id", id);
            point.put("vector", doc.vector());
            point.put("payload", payload);
            points.add(point);
        }
        client.put()
                .uri("/collections/{c}/points?wait=true", collection)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("points", points))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<VectorMatch> search(String collection, VectorSearchRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("vector", request.getQueryVector());
        body.put("limit", request.getTopK());
        body.put("with_payload", true);
        body.put("with_vector", false);
        if (request.getMinScore() > 0) body.put("score_threshold", request.getMinScore());
        if (request.getFilter() != null && !request.getFilter().isEmpty()) {
            body.put("filter", toFilter(request.getFilter()));
        }

        Map<String, Object> resp;
        try {
            resp = client.post()
                    .uri("/collections/{c}/points/search", collection)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            log.warn("Qdrant search failed for collection '{}': {}", collection, e.getMessage());
            return List.of();
        }

        Object result = resp == null ? null : resp.get("result");
        if (!(result instanceof List<?> hits)) return List.of();

        List<VectorMatch> matches = new ArrayList<>(hits.size());
        for (Object o : hits) {
            Map<String, Object> hit = (Map<String, Object>) o;
            String id = String.valueOf(hit.get("id"));
            double score = hit.get("score") instanceof Number n ? n.doubleValue() : 0;
            Map<String, Object> payload = hit.get("payload") instanceof Map<?, ?> p
                    ? (Map<String, Object>) p : Map.of();
            String content = payload.get(CONTENT_KEY) != null ? payload.get(CONTENT_KEY).toString() : null;
            Map<String, Object> metadata = new LinkedHashMap<>(payload);
            metadata.remove(CONTENT_KEY);
            matches.add(new VectorMatch(id, score, content, metadata));
        }
        return matches;
    }

    @Override
    public void delete(String collection, List<String> ids) {
        if (ids == null || ids.isEmpty()) return;
        client.post()
                .uri("/collections/{c}/points/delete?wait=true", collection)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("points", ids))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void deleteByFilter(String collection, Map<String, Object> filter) {
        if (filter == null || filter.isEmpty()) return;
        client.post()
                .uri("/collections/{c}/points/delete?wait=true", collection)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("filter", toFilter(filter)))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    @SuppressWarnings("unchecked")
    public long count(String collection) {
        try {
            Map<String, Object> resp = client.post()
                    .uri("/collections/{c}/points/count", collection)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("exact", true))
                    .retrieve()
                    .body(Map.class);
            Object result = resp == null ? null : resp.get("result");
            if (result instanceof Map<?, ?> m && m.get("count") instanceof Number n) {
                return n.longValue();
            }
            return 0;
        } catch (Exception e) {
            log.warn("Qdrant count failed for collection '{}': {}", collection, e.getMessage());
            return 0;
        }
    }

    @Override
    public String provider() {
        return "qdrant";
    }

    // ── 内部工具 ─────────────────────────────────────────────────────────────

    /** 将 Kuma 的 COSINE/EUCLID/DOT 映射为 Qdrant 取值 */
    private static String mapDistance(Distance distance) {
        return switch (distance == null ? Distance.COSINE : distance) {
            case COSINE -> "Cosine";
            case EUCLID -> "Euclid";
            case DOT -> "Dot";
        };
    }

    /** 将等值过滤 Map 转换为 Qdrant filter：{@code {must:[{key,match:{value}}]}} */
    private static Map<String, Object> toFilter(Map<String, Object> filter) {
        List<Map<String, Object>> must = new ArrayList<>(filter.size());
        filter.forEach((k, v) -> must.add(Map.of("key", k, "match", Map.of("value", v))));
        return Map.of("must", must);
    }
}
