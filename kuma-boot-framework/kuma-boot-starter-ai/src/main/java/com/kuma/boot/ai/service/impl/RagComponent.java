package com.kuma.boot.ai.service.impl;

import com.kuma.boot.ai.model.RagMatch;
import com.kuma.boot.ai.model.RagSegment;
import com.kuma.boot.ai.model.RagSource;
import com.kuma.boot.ai.model.RagStats;
import com.kuma.boot.ai.autoconfigure.properties.AiChatProperties;
import com.kuma.boot.common.model.result.PageResult;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

public class RagComponent {

    private static final Logger log = LoggerFactory.getLogger(RagComponent.class);

    /** 无 source 元数据（纯文本写入）的占位来源名 */
    private static final String UNTITLED_SOURCE = "(未命名文本)";
    /** 单次 scroll 拉取的点数 */
    private static final int SCROLL_PAGE_SIZE = 256;
    /** QdrantEmbeddingStore 默认存放段落文本的 payload 字段名 */
    private static final String PAYLOAD_TEXT_KEY = "text_segment";

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingStoreContentRetriever contentRetriever;
    private final DocumentSplitter splitter;
    private final RestClient qdrantHttpClient;
    private final String collectionName;
    private final int dimension;
    private final int defaultTopK;
    private final double defaultMinScore;

    public RagComponent(EmbeddingModel embeddingModel, AiChatProperties properties) {
        AiChatProperties.Qdrant qdrant = properties.getQdrant();
        AiChatProperties.Rag rag = properties.getRag();

        this.embeddingModel = embeddingModel;
        this.collectionName = qdrant.getCollection();
        this.dimension = qdrant.getDimension();
        this.defaultTopK = rag.getTopK();
        this.defaultMinScore = rag.getMinScore();

        this.embeddingStore = QdrantEmbeddingStore.builder()
                .host(qdrant.getHost())
                .port(qdrant.getGrpcPort())
                .collectionName(collectionName)
                .build();

        // 官方 RAG 检索器：向量化 query → 余弦相似度搜索 → 过滤低分段落
        this.contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(rag.getTopK())
                .minScore(rag.getMinScore())
                .build();

        // 官方递归切分器：优先按段落→句子→词→字符切分，保证语义完整性
        this.splitter = DocumentSplitters.recursive(rag.getChunkSize(), rag.getChunkOverlap());

        this.qdrantHttpClient = RestClient.builder()
                .baseUrl("http://" + qdrant.getHost() + ":" + qdrant.getHttpPort())
                .requestFactory(new SimpleClientHttpRequestFactory())
                .build();
    }

    @PostConstruct
    void ensureCollection() {
        try {
            qdrantHttpClient.put()
                    .uri("/collections/" + collectionName)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("vectors", Map.of("size", dimension, "distance", "Cosine")))
                    .retrieve()
                    .toBodilessEntity();
            log.info("Qdrant collection '{}' ready (dimension={})", collectionName, dimension);
        } catch (Exception e) {
            log.debug("Qdrant collection '{}': {}", collectionName, e.getMessage());
        }
    }

    /** 写入纯文本，无 source 元数据 */
    public int ingest(String text) {
        return ingest(text, null);
    }

    /** 写入纯文本，source 非空时携带 source 元数据（便于后续按来源查看 / 删除） */
    public int ingest(String text, String source) {
        Document document = (source != null && !source.isBlank())
                ? Document.from(text, metadata(source, byteLength(text)))
                : Document.from(text);
        return ingestDocument(document);
    }

    /**
     * 写入 Markdown，按 H1~H3 标题边界预分节后再分块写入。
     * 保证同一节（标题 + 正文 + 表格）不会被跨节截断。
     */
    public int ingestMarkdown(String filename, String markdown) {
        Metadata meta = metadata(filename, byteLength(markdown));
        List<String> sections = splitMarkdownBySections(markdown);
        // 先删除旧向量，再统一写入
        deleteBySource(filename);
        int total = 0;
        for (String section : sections) {
            if (section.isBlank()) continue;
            List<TextSegment> segments = splitter.split(Document.from(section, meta));
            if (segments.isEmpty()) continue;
            List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
            embeddingStore.addAll(embeddings, segments);
            total += segments.size();
        }
        log.info("Ingested {} segments from '{}' into '{}'", total, filename, collectionName);
        return total;
    }

    /** 按 H1/H2/H3 标题行拆分 Markdown，每节包含标题及其后续内容 */
    private static List<String> splitMarkdownBySections(String markdown) {
        List<String> sections = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String line : markdown.split("\n", -1)) {
            if (line.matches("^#{1,3} .+") && !current.isEmpty()) {
                sections.add(current.toString());
                current = new StringBuilder();
            }
            current.append(line).append("\n");
        }
        if (!current.isEmpty()) {
            sections.add(current.toString());
        }
        return sections.isEmpty() ? List.of(markdown) : sections;
    }

    /**
     * 通过 Apache Tika 解析任意格式文件流（PDF / Word / PPT / Excel / HTML 等），
     * 提取纯文本后切块写入向量库。
     */
    public int ingestStream(String filename, InputStream inputStream) {
        try {
            byte[] bytes = inputStream.readAllBytes();
            Document parsed = new ApacheTikaDocumentParser().parse(new ByteArrayInputStream(bytes));
            Document document = Document.from(parsed.text(), metadata(filename, bytes.length));
            return ingestDocument(document);
        } catch (IOException e) {
            throw new RuntimeException("读取文件流失败: " + filename, e);
        }
    }

    /** 构建带来源、写入时间、原始大小的元数据；这些字段会随段落一并写入向量库 payload */
    private static Metadata metadata(String source, long size) {
        return new Metadata()
                .put("source", source)
                .put("ingestedAt", System.currentTimeMillis())
                .put("size", size);
    }

    private static long byteLength(String text) {
        return text == null ? 0 : text.getBytes(StandardCharsets.UTF_8).length;
    }

    /** 暴露官方检索器，供 AiServices Agent 绑定（自动 RAG） */
    public ContentRetriever contentRetriever() {
        return contentRetriever;
    }

    /**
     * 检索与 query 最相关的段落，拼接为上下文字符串。
     * topK 和 minScore 均由配置控制（kuma.boot.ai.rag.*）。
     */
    public String retrieveContext(String query) {
        List<Content> contents = contentRetriever.retrieve(Query.from(query));
        if (contents.isEmpty()) return "";
        return contents.stream()
                .map(c -> c.textSegment().text())
                .collect(Collectors.joining("\n\n"));
    }

    /**
     * 列出知识库中已写入的所有文档来源（按 {@code source} 元数据聚合），
     * 并统计每个来源的段落数。通过 Qdrant scroll API 全量遍历点。
     */
    @SuppressWarnings("unchecked")
    public List<RagSource> listSources() {
        // value = [段落数, ingestedAt, size]，同来源的元数据取首个非零值
        Map<String, long[]> agg = new LinkedHashMap<>();
        Object offset = null;
        do {
            Map<String, Object> body = new HashMap<>();
            body.put("limit", SCROLL_PAGE_SIZE);
            body.put("with_payload", List.of("source", "ingestedAt", "size"));
            body.put("with_vector", false);
            if (offset != null) body.put("offset", offset);

            Map<String, Object> resp;
            try {
                resp = qdrantHttpClient.post()
                        .uri("/collections/" + collectionName + "/points/scroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)
                        .retrieve()
                        .body(Map.class);
            } catch (Exception e) {
                log.warn("Qdrant scroll failed for collection '{}': {}", collectionName, e.getMessage());
                break;
            }

            Map<String, Object> result = resp == null ? null : (Map<String, Object>) resp.get("result");
            if (result == null) break;

            List<Map<String, Object>> points = (List<Map<String, Object>>) result.get("points");
            if (points != null) {
                for (Map<String, Object> point : points) {
                    Map<String, Object> payload = (Map<String, Object>) point.get("payload");
                    Object source = payload == null ? null : payload.get("source");
                    String key = source != null ? source.toString() : UNTITLED_SOURCE;
                    long[] v = agg.computeIfAbsent(key, k -> new long[3]);
                    v[0]++;
                    if (payload != null) {
                        if (v[1] == 0 && payload.get("ingestedAt") instanceof Number n) v[1] = n.longValue();
                        if (v[2] == 0 && payload.get("size") instanceof Number n) v[2] = n.longValue();
                    }
                }
            }
            offset = result.get("next_page_offset");
        } while (offset != null);

        return agg.entrySet().stream()
                .map(e -> {
                    long[] v = e.getValue();
                    return new RagSource(e.getKey(), (int) v[0],
                            v[1] > 0 ? v[1] : null, v[2] > 0 ? v[2] : null);
                })
                .collect(Collectors.toList());
    }

    /**
     * 分页列出指定来源的段落文本，供管理界面下钻预览切分结果。
     *
     * <p>Qdrant scroll 为游标分页，这里先用「不取 payload」的廉价 scroll 跳过前 {@code page}
     * 页，再仅对目标页取段落文本，避免把整个文档全部读入内存。
     *
     * @param source 来源标识
     * @param page   页码，从 0 开始
     * @param size   每页条数（1~200，越界自动修正）
     */
    @SuppressWarnings("unchecked")
    public PageResult<RagSegment> listSegments(String source, int page, int size) {
        int p = Math.max(page, 0);
        int s = size <= 0 ? 50 : Math.min(size, 200);
        if (source == null || source.isBlank()) {
            return PageResult.of(0, 0, p, s, List.of());
        }

        long total = countBySource(source);
        int totalPage = (int) ((total + s - 1) / s);
        if (total == 0 || p >= totalPage) {
            return PageResult.of(total, totalPage, p, s, List.of());
        }

        // 跳过前 p 页（不取 payload，仅推进游标）
        Object offset = null;
        for (int i = 0; i < p; i++) {
            offset = scrollPage(source, offset, s, false)[1];
            if (offset == null) {
                return PageResult.of(total, totalPage, p, s, List.of());
            }
        }

        // 取目标页文本
        Object[] target = scrollPage(source, offset, s, true);
        List<Map<String, Object>> points = (List<Map<String, Object>>) target[0];
        List<RagSegment> data = new ArrayList<>();
        int index = p * s;
        for (Map<String, Object> point : points) {
            Map<String, Object> payload = (Map<String, Object>) point.get("payload");
            Object text = payload == null ? null : payload.get(PAYLOAD_TEXT_KEY);
            data.add(new RagSegment(source, index++, text != null ? text.toString() : ""));
        }
        return PageResult.of(total, totalPage, p, s, data);
    }

    /**
     * 执行一次按 source 过滤的 scroll。
     *
     * @param withText 是否带回段落文本 payload（跳页时为 false 以降低开销）
     * @return {@code [points(List<Map>), nextPageOffset(Object|null)]}
     */
    @SuppressWarnings("unchecked")
    private Object[] scrollPage(String source, Object offset, int limit, boolean withText) {
        Map<String, Object> body = new HashMap<>();
        body.put("limit", limit);
        body.put("with_payload", withText ? List.of(PAYLOAD_TEXT_KEY) : false);
        body.put("with_vector", false);
        body.put("filter", sourceFilter(source));
        if (offset != null) body.put("offset", offset);

        Map<String, Object> resp = qdrantHttpClient.post()
                .uri("/collections/" + collectionName + "/points/scroll")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

        Map<String, Object> result = resp == null ? null : (Map<String, Object>) resp.get("result");
        if (result == null) return new Object[]{List.of(), null};
        List<Map<String, Object>> points = (List<Map<String, Object>>) result.get("points");
        return new Object[]{points != null ? points : List.of(), result.get("next_page_offset")};
    }

    /**
     * 检索测试：返回命中的段落及相似度分数，供管理界面调参。
     * topK / minScore 为 null 时使用配置默认值（kuma.boot.ai.rag.*）。
     */
    public List<RagMatch> retrieve(String query, Integer topK, Double minScore) {
        if (query == null || query.isBlank()) return List.of();
        int k = (topK != null && topK > 0) ? topK : defaultTopK;
        double min = (minScore != null) ? minScore : defaultMinScore;

        Embedding queryEmbedding = embeddingModel.embed(query).content();
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(k)
                .minScore(min)
                .build();
        EmbeddingSearchResult<TextSegment> result = embeddingStore.search(request);

        return result.matches().stream()
                .map(m -> {
                    TextSegment seg = m.embedded();
                    String src = (seg != null) ? seg.metadata().getString("source") : null;
                    String text = (seg != null) ? seg.text() : "";
                    return new RagMatch(src != null ? src : UNTITLED_SOURCE, text, m.score());
                })
                .collect(Collectors.toList());
    }

    /** 知识库概览统计（来源数 / 段落数 / 向量维度 / collection 状态）。 */
    public RagStats stats() {
        int totalSegments = countAll(null);
        int totalSources = listSources().size();
        return new RagStats(totalSources, totalSegments, dimension, collectionStatus());
    }

    @SuppressWarnings("unchecked")
    private String collectionStatus() {
        try {
            Map<String, Object> resp = qdrantHttpClient.get()
                    .uri("/collections/" + collectionName)
                    .retrieve()
                    .body(Map.class);
            Map<String, Object> result = resp == null ? null : (Map<String, Object>) resp.get("result");
            Object status = result == null ? null : result.get("status");
            return status != null ? status.toString() : "unknown";
        } catch (Exception e) {
            return "unavailable";
        }
    }

    /**
     * 删除指定来源（{@code source=filename}）的所有段落，返回删除的段落数。
     * 使用官方 EmbeddingStore 的元数据过滤删除能力。
     */
    public int deleteBySource(String source) {
        if (source == null || source.isBlank()) return 0;
        int count = countBySource(source);
        if (count == 0) return 0;
        embeddingStore.removeAll(metadataKey("source").isEqualTo(source));
        log.info("Deleted {} segments of source '{}' from '{}'", count, source, collectionName);
        return count;
    }

    /** 清空整个知识库（删除当前 collection 下所有段落），返回删除的段落数。 */
    public int clearAll() {
        int total = countAll(null);
        embeddingStore.removeAll();
        log.info("Cleared all {} segments from '{}'", total, collectionName);
        return total;
    }

    /** 统计指定来源的段落数 */
    private int countBySource(String source) {
        return countAll(sourceFilter(source));
    }

    /** 构建 Qdrant 按 source 精确匹配的过滤条件 */
    private static Map<String, Object> sourceFilter(String source) {
        return Map.of("must", List.of(
                Map.of("key", "source", "match", Map.of("value", source))));
    }

    /** 调用 Qdrant count API 统计点数；filter 为 null 时统计全量 */
    @SuppressWarnings("unchecked")
    private int countAll(Map<String, Object> filter) {
        Map<String, Object> body = new HashMap<>();
        body.put("exact", true);
        if (filter != null) body.put("filter", filter);

        try {
            Map<String, Object> resp = qdrantHttpClient.post()
                    .uri("/collections/" + collectionName + "/points/count")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            Map<String, Object> result = resp == null ? null : (Map<String, Object>) resp.get("result");
            if (result == null) return 0;
            Number count = (Number) result.get("count");
            return count == null ? 0 : count.intValue();
        } catch (Exception e) {
            log.warn("Qdrant count failed for collection '{}': {}", collectionName, e.getMessage());
            return 0;
        }
    }

    private int ingestDocument(Document document) {
        List<TextSegment> segments = splitter.split(document);
        if (segments.isEmpty()) return 0;
        String source = document.metadata().getString("source");
        // 覆盖语义：带 source 的文档重复写入时，先删除旧段落，避免段落翻倍
        if (source != null && !source.isBlank()) {
            deleteBySource(source);
        }
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        embeddingStore.addAll(embeddings, segments);
        log.info("Ingested {} segments from '{}' into '{}'",
                segments.size(), source != null ? source : "text", collectionName);
        return segments.size();
    }
}
