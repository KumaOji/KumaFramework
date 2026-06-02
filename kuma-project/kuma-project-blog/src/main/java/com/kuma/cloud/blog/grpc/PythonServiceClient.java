package com.kuma.cloud.blog.grpc;

import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * Python gRPC 服务调用入口
 *
 * <p>当 {@code kuma.boot.grpc.client.host} 配置后自动激活。
 * 在完成 proto 代码生成（generateProto）后，按以下步骤启用各方法：
 * <ol>
 *   <li>注入对应的 BlockingStub（在 {@link PythonGrpcClientConfig} 中取消注释）</li>
 *   <li>取消下方各方法的注释，补充异常处理逻辑</li>
 * </ol>
 */
@Component
@ConditionalOnBean(ManagedChannel.class)
@RequiredArgsConstructor
public class PythonServiceClient {

    private final ManagedChannel pythonServiceChannel;

    // ── TextAnalysisService ────────────────────────────────────────────────

    // private final TextAnalysisServiceGrpc.TextAnalysisServiceBlockingStub textAnalysisStub;

    // /** 提取文章关键词 */
    // public List<String> extractKeywords(String text) {
    //     KeywordsReply reply = textAnalysisStub.extractKeywords(
    //             TextRequest.newBuilder().setText(text).setLanguage("zh").build());
    //     return reply.getKeywordsList();
    // }

    // /** 自动生成文章摘要 */
    // public String summarize(String text) {
    //     SummaryReply reply = textAnalysisStub.summarizeText(
    //             TextRequest.newBuilder().setText(text).build());
    //     return reply.getSummary();
    // }

    // /** 情感分析，返回 positive / negative / neutral */
    // public String analyzeSentiment(String text) {
    //     SentimentReply reply = textAnalysisStub.analyzeSentiment(
    //             TextRequest.newBuilder().setText(text).build());
    //     return reply.getLabel();
    // }

    // ── RecommendService ───────────────────────────────────────────────────

    // private final RecommendServiceGrpc.RecommendServiceBlockingStub recommendStub;

    // /** 根据文章 ID 推荐相似文章，返回推荐的文章 ID 列表 */
    // public List<Long> recommendByArticle(Long articleId, int topN) {
    //     RecommendReply reply = recommendStub.recommendByArticle(
    //             RecommendRequest.newBuilder().setArticleId(articleId).setTopN(topN).build());
    //     return reply.getArticleIdsList();
    // }
}
