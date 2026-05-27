package com.kuma.cloud.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kuma.cloud.blog.domain.entity.ChatHistory;
import com.kuma.cloud.blog.domain.entity.ChatRoom;
import com.kuma.cloud.blog.domain.vo.ChatRoomVO;
import com.kuma.cloud.blog.mapper.ChatHistoryMapper;
import com.kuma.cloud.blog.mapper.ChatRoomMapper;
import com.kuma.cloud.blog.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomMapper chatRoomMapper;
    private final ChatHistoryMapper chatHistoryMapper;

    @Override
    public List<ChatRoomVO> listActive() {
        QueryWrapper<ChatRoom> qw = new QueryWrapper<>();
        qw.eq("status", 1).orderByAsc("sort_order");
        List<ChatRoom> rooms = chatRoomMapper.selectList(qw);
        return rooms.stream().map(r -> {
            ChatRoomVO vo = new ChatRoomVO();
            BeanUtils.copyProperties(r, vo);
            return vo;
        }).toList();
    }

    @Override
    public ChatRoom getById(Long id) {
        return chatRoomMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ChatRoom chatRoom) {
        LocalDateTime now = LocalDateTime.now();
        chatRoom.setCreateTime(now);
        chatRoom.setUpdateTime(now);
        if (chatRoom.getSortOrder() == null) chatRoom.setSortOrder(0);
        if (chatRoom.getStatus() == null) chatRoom.setStatus(1);
        chatRoomMapper.insert(chatRoom);
        return chatRoom.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(ChatRoom chatRoom) {
        chatRoom.setUpdateTime(LocalDateTime.now());
        return chatRoomMapper.updateById(chatRoom) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        ChatRoom room = new ChatRoom();
        room.setId(id);
        room.setStatus(0);
        room.setUpdateTime(LocalDateTime.now());
        return chatRoomMapper.updateById(room) > 0;
    }

    @Override
    public List<ChatHistory> getRecentHistory(Long roomId, int limit) {
        List<ChatHistory> list = chatHistoryMapper.selectRecentByRoomId(roomId, limit);
        Collections.reverse(list);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveHistory(ChatHistory history) {
        history.setCreateTime(LocalDateTime.now());
        chatHistoryMapper.insert(history);
    }
}
