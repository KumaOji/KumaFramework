package com.kuma.boot.ai.service.impl;

import com.kuma.boot.ai.autoconfigure.properties.AiChatProperties;
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
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RagComponent {

    private static final Logger log = LoggerFactory.getLogger(RagComponent.class);

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingStoreContentRetriever contentRetriever;
    private final DocumentSplitter splitter;
    private final RestClient qdrantHttpClient;
    private final String collectionName;
    private final int dimension;

    public RagComponent(EmbeddingModel embeddingModel, AiChatProperties properties) {
        AiChatProperties.Qdrant qdrant = properties.getQdrant();
        AiChatProperties.Rag rag = properties.getRag();

        this.embeddingModel = embeddingModel;
        this.collectionName = qdrant.getCollection();
        this.dimension = qdrant.getDimension();

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
        return ingestDocument(Document.from(text));
    }

    /** 写入 Markdown，携带 source=filename 元数据 */
    public int ingestMarkdown(String filename, String markdown) {
        return ingestDocument(Document.from(markdown, Metadata.from("source", filename)));
    }

    /**
     * 通过 Apache Tika 解析任意格式文件流（PDF / Word / PPT / Excel / HTML 等），
     * 提取纯文本后切块写入向量库。
     */
    public int ingestStream(String filename, InputStream inputStream) {
        Document parsed = new ApacheTikaDocumentParser().parse(inputStream);
        Document document = Document.from(parsed.text(), Metadata.from("source", filename));
        return ingestDocument(document);
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

    private int ingestDocument(Document document) {
        List<TextSegment> segments = splitter.split(document);
        if (segments.isEmpty()) return 0;
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        embeddingStore.addAll(embeddings, segments);
        String label = document.metadata().getString("source");
        log.info("Ingested {} segments from '{}' into '{}'",
                segments.size(), label != null ? label : "text", collectionName);
        return segments.size();
    }
}
