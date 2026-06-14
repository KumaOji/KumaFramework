package com.kuma.cloud.blog.service;

import com.kuma.cloud.blog.domain.vo.RagSessionVO;

import java.util.List;

public interface RagSessionService {

    /** 创建会话并持久化到 DB，返回完整 VO */
    RagSessionVO create(String title);

    /** 列出所有会话（DB 记录），并标记哪些在内存中仍活跃 */
    List<RagSessionVO> listAll();

    /** 重命名会话标题 */
    void rename(String sessionId, String title);

    /** 删除会话（DB + 内存） */
    void delete(String sessionId);

    /** 首次使用 sessionId 发起对话时，若 DB 中不存在则自动创建（默认标题） */
    void ensureExists(String sessionId);
}
