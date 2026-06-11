package com.kuma.cloud.blog.service;

import com.kuma.cloud.blog.domain.entity.ChatHistory;
import com.kuma.cloud.blog.domain.entity.ChatRoom;
import com.kuma.cloud.blog.domain.vo.ChatRoomVO;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatRoomService {

    List<ChatRoomVO> listActive();

    /** 管理员视角：返回全部聊天空间（含已停用），按 sort_order 升序 */
    List<ChatRoom> listAll();

    ChatRoom getById(Long id);

    Long create(ChatRoom chatRoom);

    boolean update(ChatRoom chatRoom);

    boolean delete(Long id);

    List<ChatHistory> getRecentHistory(Long roomId, int limit);

    void saveHistory(ChatHistory history);

    /**
     * 清理指定聊天空间的历史记录。
     *
     * @param roomId 聊天空间 ID
     * @param before 仅删除该时间之前的记录；为 {@code null} 时清空全部历史
     * @return 删除的记录条数
     */
    int clearHistory(Long roomId, LocalDateTime before);

    /**
     * 删除单条聊天记录（管理员撤回 / 内容审核）。
     *
     * @return 被删除的记录，若不存在返回 {@code null}
     */
    ChatHistory deleteMessage(Long messageId);
}
