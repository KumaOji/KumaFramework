package com.kuma.cloud.blog.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.security.spring.access.expression.Authorize;
import com.kuma.cloud.blog.domain.entity.ChatHistory;
import com.kuma.cloud.blog.domain.entity.ChatRoom;
import com.kuma.cloud.blog.domain.vo.ChatMessageVO;
import com.kuma.cloud.blog.domain.vo.ChatOnlineUserVO;
import com.kuma.cloud.blog.domain.vo.ChatRoomVO;
import com.kuma.cloud.blog.security.BlogPermissions;
import com.kuma.cloud.blog.service.ChatRoomService;
import com.kuma.cloud.blog.websocket.OnlineUserRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "聊天室")
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final OnlineUserRegistry onlineUserRegistry;

    @Operation(summary = "获取聊天空间列表")
    @GetMapping("/room/list")
    public Result<List<ChatRoomVO>> list() {
        List<ChatRoomVO> rooms = chatRoomService.listActive();
        rooms.forEach(r -> r.setOnlineCount(onlineUserRegistry.getOnlineCount(r.getId())));
        return Result.success(rooms);
    }

    @Operation(summary = "获取聊天室历史消息（最近50条）")
    @GetMapping("/room/{roomId}/history")
    public Result<List<ChatMessageVO>> history(@PathVariable Long roomId) {
        List<ChatHistory> histories = chatRoomService.getRecentHistory(roomId, 50);
        List<ChatMessageVO> result = histories.stream().map(h -> {
            ChatMessageVO vo = new ChatMessageVO();
            vo.setType(h.getMessageType());
            vo.setRoomId(h.getRoomId());
            vo.setUserId(h.getUserId());
            vo.setNickname(h.getNickname());
            vo.setAvatar(h.getAvatar());
            vo.setContent(h.getContent());
            vo.setTimestamp(h.getCreateTime());
            return vo;
        }).toList();
        return Result.success(result);
    }

    @Operation(summary = "获取聊天室在线用户")
    @GetMapping("/room/{roomId}/online")
    public Result<List<ChatOnlineUserVO>> online(@PathVariable Long roomId) {
        List<ChatOnlineUserVO> users = onlineUserRegistry.getOnlineUsers(roomId).stream().map(u -> {
            ChatOnlineUserVO vo = new ChatOnlineUserVO();
            vo.setUserId(u.getUserId());
            vo.setNickname(u.getNickname());
            vo.setAvatar(u.getAvatar());
            return vo;
        }).toList();
        return Result.success(users);
    }

    @Operation(summary = "创建聊天空间（管理员）")
    @PostMapping("/room")
    @Authorize(BlogPermissions.CHAT_CREATE)
    public Result<Long> create(@RequestBody ChatRoom chatRoom) {
        return Result.success(chatRoomService.create(chatRoom));
    }

    @Operation(summary = "更新聊天空间（管理员）")
    @PutMapping("/room/{id}")
    @Authorize(BlogPermissions.CHAT_UPDATE)
    public Result<Boolean> update(@PathVariable Long id, @RequestBody ChatRoom chatRoom) {
        chatRoom.setId(id);
        return Result.success(chatRoomService.update(chatRoom));
    }

    @Operation(summary = "删除聊天空间（管理员）")
    @DeleteMapping("/room/{id}")
    @Authorize(BlogPermissions.CHAT_DELETE)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(chatRoomService.delete(id));
    }
}
