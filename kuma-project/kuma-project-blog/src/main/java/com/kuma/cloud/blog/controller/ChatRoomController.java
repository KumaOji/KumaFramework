package com.kuma.cloud.blog.controller;

import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.security.spring.access.expression.Authorize;
import com.kuma.cloud.blog.domain.entity.ChatBlacklist;
import com.kuma.cloud.blog.domain.entity.ChatHistory;
import com.kuma.cloud.blog.domain.entity.ChatRoom;
import com.kuma.cloud.blog.domain.dto.BlacklistAddDTO;
import com.kuma.cloud.blog.domain.vo.ChatMessageVO;
import com.kuma.cloud.blog.domain.vo.ChatOnlineUserVO;
import com.kuma.cloud.blog.domain.vo.ChatRoomOnlineVO;
import com.kuma.cloud.blog.domain.vo.ChatRoomVO;
import com.kuma.cloud.blog.security.BlogPermissions;
import com.kuma.cloud.blog.service.ChatBlacklistService;
import com.kuma.cloud.blog.service.ChatRoomService;
import com.kuma.cloud.blog.websocket.OnlineUserRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "聊天室")
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final OnlineUserRegistry onlineUserRegistry;
    private final ChatBlacklistService chatBlacklistService;
    private final SimpMessagingTemplate messagingTemplate;

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
            vo.setId(h.getId());
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

    @Operation(summary = "全部在线用户总览（管理员，按聊天空间分组）")
    @GetMapping("/online/all")
    @Authorize(BlogPermissions.CHAT_UPDATE)
    public Result<List<ChatRoomOnlineVO>> onlineAll() {
        Map<Long, String> roomNames = chatRoomService.listAll().stream()
                .collect(Collectors.toMap(ChatRoom::getId, ChatRoom::getName, (a, b) -> a));

        List<ChatRoomOnlineVO> result = onlineUserRegistry.getAllOnlineUsers().entrySet().stream()
                .map(entry -> {
                    Long roomId = entry.getKey();
                    List<ChatOnlineUserVO> users = entry.getValue().stream().map(u -> {
                        ChatOnlineUserVO vo = new ChatOnlineUserVO();
                        vo.setUserId(u.getUserId());
                        vo.setNickname(u.getNickname());
                        vo.setAvatar(u.getAvatar());
                        return vo;
                    }).toList();

                    ChatRoomOnlineVO vo = new ChatRoomOnlineVO();
                    vo.setRoomId(roomId);
                    vo.setRoomName(roomNames.get(roomId));
                    vo.setOnlineCount(users.size());
                    vo.setUsers(users);
                    return vo;
                }).toList();
        return Result.success(result);
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

    // ── 历史记录管理 ────────────────────────────────────────────────────────

    @Operation(summary = "聊天空间列表（管理员，含已停用）")
    @GetMapping("/room/admin/list")
    @Authorize(BlogPermissions.CHAT_UPDATE)
    public Result<List<ChatRoom>> adminList() {
        return Result.success(chatRoomService.listAll());
    }

    @Operation(summary = "清理聊天空间历史记录（管理员，before 为空则清空全部）")
    @DeleteMapping("/room/{roomId}/history")
    @Authorize(BlogPermissions.CHAT_HISTORY)
    public Result<Integer> clearHistory(
            @PathVariable Long roomId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime before) {
        int deleted = chatRoomService.clearHistory(roomId, before);

        // 通知在线客户端清空（或刷新）本地消息列表
        ChatMessageVO event = new ChatMessageVO();
        event.setType("CLEAR");
        event.setRoomId(roomId);
        event.setContent(before == null ? "管理员已清空聊天记录" : "管理员已清理历史聊天记录");
        event.setTimestamp(LocalDateTime.now());
        messagingTemplate.convertAndSend("/topic/chat." + roomId, event);

        return Result.success(deleted);
    }

    @Operation(summary = "删除单条聊天记录（管理员撤回 / 审核）")
    @DeleteMapping("/message/{id}")
    @Authorize(BlogPermissions.CHAT_HISTORY)
    public Result<Boolean> deleteMessage(@PathVariable Long id) {
        ChatHistory removed = chatRoomService.deleteMessage(id);
        if (removed == null) {
            return Result.success(false);
        }

        // 通知在线客户端移除该条消息
        ChatMessageVO event = new ChatMessageVO();
        event.setId(removed.getId());
        event.setType("DELETE");
        event.setRoomId(removed.getRoomId());
        event.setTimestamp(LocalDateTime.now());
        messagingTemplate.convertAndSend("/topic/chat." + removed.getRoomId(), event);

        return Result.success(true);
    }

    // ── 黑名单管理 ──────────────────────────────────────────────────────────

    @Operation(summary = "查询黑名单列表（管理员）")
    @GetMapping("/blacklist")
    @Authorize(BlogPermissions.CHAT_BLACKLIST)
    public Result<List<ChatBlacklist>> blacklistList() {
        return Result.success(chatBlacklistService.list());
    }

    @Operation(summary = "添加邮箱到黑名单（管理员）")
    @PostMapping("/blacklist")
    @Authorize(BlogPermissions.CHAT_BLACKLIST)
    public Result<Void> blacklistAdd(@RequestBody BlacklistAddDTO request) {
        chatBlacklistService.add(request.getEmail(), request.getReason());
        return Result.success();
    }

    @Operation(summary = "移除黑名单（管理员）")
    @DeleteMapping("/blacklist/{id}")
    @Authorize(BlogPermissions.CHAT_BLACKLIST)
    public Result<Void> blacklistRemove(@PathVariable Long id) {
        chatBlacklistService.remove(id);
        return Result.success();
    }
}
