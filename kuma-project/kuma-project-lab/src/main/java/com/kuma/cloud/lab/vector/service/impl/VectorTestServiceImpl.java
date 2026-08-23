package com.kuma.cloud.lab.vector.service.impl;

import com.kuma.boot.data.vector.core.VectorStore;
import com.kuma.boot.data.vector.core.model.VectorDocument;
import com.kuma.boot.data.vector.core.model.VectorMatch;
import com.kuma.boot.data.vector.core.model.VectorSearchRequest;
import com.kuma.cloud.lab.vector.config.VectorLabProperties;
import com.kuma.cloud.lab.vector.domain.dto.VectorSearchDTO;
import com.kuma.cloud.lab.vector.domain.dto.VectorUpsertDTO;
import com.kuma.cloud.lab.vector.domain.vo.VectorDocumentVO;
import com.kuma.cloud.lab.vector.domain.vo.VectorMatchVO;
import com.kuma.cloud.lab.vector.domain.vo.VectorOperationStepVO;
import com.kuma.cloud.lab.vector.domain.vo.VectorScenarioVO;
import com.kuma.cloud.lab.vector.domain.vo.VectorStoreStatusVO;
import com.kuma.cloud.lab.vector.service.VectorTestService;
import com.kuma.cloud.lab.vector.support.VectorLabEmbedding;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VectorTestServiceImpl implements VectorTestService {

    private final VectorStore vectorStore;
    private final VectorLabProperties vectorLabProperties;

    @Override
    public VectorScenarioVO runScenario() {
        String collection = vectorLabProperties.getCollection();
        int dimension = vectorLabProperties.getDimension();

        resetCollection();
        ensureCollection();

        List<VectorOperationStepVO> steps = new ArrayList<>();

        List<VectorDocument> seedDocuments = List.of(
                new VectorDocument("java", scenarioVector(1, 0, 0, 0), "Java 是一种面向对象编程语言",
                        Map.of("category", "language")),
                new VectorDocument("python", scenarioVector(0.9f, 0.1f, 0, 0), "Python 常用于数据分析与机器学习",
                        Map.of("category", "language")),
                new VectorDocument("recipe", scenarioVector(0, 0, 1, 0), "红烧肉是一道经典中式菜肴",
                        Map.of("category", "food"))
        );
        vectorStore.upsert(collection, seedDocuments);
        steps.add(step("UPSERT seed documents", seedDocuments.size(), vectorStore.count(collection)));

        VectorSearchRequest languageSearch = VectorSearchRequest.builder()
                .queryVector(scenarioVector(0.95f, 0.05f, 0, 0))
                .topK(2)
                .build();
        List<VectorMatchVO> topLanguageMatches = toMatchViews(vectorStore.search(collection, languageSearch));
        steps.add(step("SEARCH programming query", Map.of("topK", 2), topLanguageMatches));

        VectorSearchRequest filteredSearch = VectorSearchRequest.builder()
                .queryVector(scenarioVector(0.5f, 0.5f, 0.5f, 0))
                .topK(5)
                .filter(Map.of("category", "language"))
                .build();
        List<VectorMatchVO> filteredMatches = toMatchViews(vectorStore.search(collection, filteredSearch));
        steps.add(step("SEARCH with metadata filter", Map.of("category", "language"), filteredMatches));

        vectorStore.delete(collection, List.of("recipe"));
        steps.add(step("DELETE recipe document", "recipe", vectorStore.count(collection)));

        return new VectorScenarioVO(collection, vectorStore.provider(), dimension, steps);
    }

    @Override
    public VectorDocumentVO upsert(VectorUpsertDTO dto) {
        ensureCollection();
        float[] vector = resolveVector(dto.getVector(), dto.getContent());
        VectorDocument document = new VectorDocument(
                dto.getId(),
                vector,
                dto.getContent(),
                dto.getMetadata() == null ? Map.of() : dto.getMetadata()
        );
        vectorStore.upsert(vectorLabProperties.getCollection(), document);
        return toDocumentView(document);
    }

    @Override
    public List<VectorMatchVO> search(VectorSearchDTO dto) {
        ensureCollection();
        float[] queryVector = resolveQueryVector(dto);
        VectorSearchRequest request = VectorSearchRequest.builder()
                .queryVector(queryVector)
                .topK(dto.getTopK())
                .minScore(dto.getMinScore() == null ? 0.0 : dto.getMinScore())
                .filter(dto.getFilter())
                .build();
        return toMatchViews(vectorStore.search(vectorLabProperties.getCollection(), request));
    }

    @Override
    public long delete(String id) {
        ensureCollection();
        vectorStore.delete(vectorLabProperties.getCollection(), List.of(id));
        return vectorStore.count(vectorLabProperties.getCollection());
    }

    @Override
    public long resetCollection() {
        String collection = vectorLabProperties.getCollection();
        if (vectorStore.collectionExists(collection)) {
            vectorStore.deleteCollection(collection);
        }
        return 0;
    }

    @Override
    public VectorStoreStatusVO status() {
        String collection = vectorLabProperties.getCollection();
        return new VectorStoreStatusVO(
                vectorStore.provider(),
                collection,
                vectorStore.collectionExists(collection),
                vectorStore.count(collection),
                vectorLabProperties.getDimension()
        );
    }

    private void ensureCollection() {
        String collection = vectorLabProperties.getCollection();
        if (!vectorStore.collectionExists(collection)) {
            vectorStore.createCollection(collection, vectorLabProperties.getDimension(), vectorLabProperties.getDistance());
        }
    }

    private float[] resolveVector(List<Float> vector, String content) {
        if (vector != null && !vector.isEmpty()) {
            return VectorLabEmbedding.toArray(vector, vectorLabProperties.getDimension());
        }
        return VectorLabEmbedding.embed(content, vectorLabProperties.getDimension());
    }

    private float[] resolveQueryVector(VectorSearchDTO dto) {
        if (dto.getQueryVector() != null && !dto.getQueryVector().isEmpty()) {
            return VectorLabEmbedding.toArray(dto.getQueryVector(), vectorLabProperties.getDimension());
        }
        if (!StringUtils.hasText(dto.getQuery())) {
            throw new IllegalArgumentException("query 与 queryVector 不能同时为空");
        }
        return VectorLabEmbedding.embed(dto.getQuery(), vectorLabProperties.getDimension());
    }

    private float[] scenarioVector(float... values) {
        int dimension = vectorLabProperties.getDimension();
        if (values.length != dimension) {
            throw new IllegalStateException("场景向量维度应为 " + dimension);
        }
        return values;
    }

    private VectorOperationStepVO step(String operation, Object input, Object output) {
        return new VectorOperationStepVO(operation, input, output);
    }

    private List<VectorMatchVO> toMatchViews(List<VectorMatch> matches) {
        return matches.stream()
                .map(match -> new VectorMatchVO(match.id(), match.score(), match.content(), match.metadata()))
                .toList();
    }

    private VectorDocumentVO toDocumentView(VectorDocument document) {
        return new VectorDocumentVO(
                document.id(),
                document.content(),
                VectorLabEmbedding.toList(document.vector()),
                document.metadata() == null ? Map.of() : new LinkedHashMap<>(document.metadata())
        );
    }

}
