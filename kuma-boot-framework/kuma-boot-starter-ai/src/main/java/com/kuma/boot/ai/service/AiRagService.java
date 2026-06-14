package com.kuma.boot.ai.service;

import com.kuma.boot.ai.model.AiChatRequest;
import com.kuma.boot.ai.model.RagMatch;
import com.kuma.boot.ai.model.RagSegment;
import com.kuma.boot.ai.model.RagSource;
import com.kuma.boot.ai.model.RagStats;
import com.kuma.boot.common.model.result.PageResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AiRagService {

    /** 将文本分段后写入向量库，返回写入段落数 */
    int ingest(String text);

    /** 将文本分段后写入向量库，并携带 source 来源标识，返回写入段落数 */
    int ingest(String text, String source);

    /** 将 Markdown 文件按标题切段后写入向量库，返回写入段落数 */
    int ingestMarkdown(String filename, String markdown);

    /** 通过 Tika 解析任意格式文件（PDF/Word/PPT/Excel/HTML 等）后写入向量库，返回写入段落数 */
    int ingestFile(String filename, InputStream inputStream);

    /** 列出知识库中已上传的所有文档来源及其段落数 */
    List<RagSource> listSources();

    /** 分页列出指定来源的段落文本（下钻预览切分结果），page 从 0 开始 */
    PageResult<RagSegment> listSegments(String source, int page, int size);

    /** 检索测试：返回命中段落及相似度分数，topK / minScore 为 null 时用配置默认值 */
    List<RagMatch> retrieve(String query, Integer topK, Double minScore);

    /** 知识库概览统计 */
    RagStats stats();

    /** 删除指定来源的所有段落，返回删除的段落数 */
    int deleteSource(String source);

    /** 清空整个知识库，返回删除的段落数 */
    int clearAll();

    /** RAG 增强的非流式对话 */
    Map<String, Object> chat(AiChatRequest request);

    /** RAG 增强的流式对话（SSE） */
    SseEmitter streamChat(AiChatRequest request);

    /** 列出所有活跃 RAG 会话的 sessionId */
    Set<String> listSessions();

    /** 清除指定 session 的对话历史 */
    void clearMemory(String sessionId);
}
