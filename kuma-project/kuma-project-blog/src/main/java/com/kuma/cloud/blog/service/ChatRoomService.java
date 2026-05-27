package com.kuma.cloud.blog.service;

import com.kuma.cloud.blog.domain.entity.ChatHistory;
import com.kuma.cloud.blog.domain.entity.ChatRoom;
import com.kuma.cloud.blog.domain.vo.ChatRoomVO;

import java.util.List;

public interface ChatRoomService {

    List<ChatRoomVO> listActive();

    ChatRoom getById(Long id);

    Long create(ChatRoom chatRoom);

    boolean update(ChatRoom chatRoom);

    boolean delete(Long id);

    List<ChatHistory> getRecentHistory(Long roomId, int limit);

    void saveHistory(ChatHistory history);
}
