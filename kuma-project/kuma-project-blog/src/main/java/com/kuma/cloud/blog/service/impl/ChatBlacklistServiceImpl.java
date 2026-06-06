package com.kuma.cloud.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kuma.cloud.blog.domain.entity.ChatBlacklist;
import com.kuma.cloud.blog.mapper.ChatBlacklistMapper;
import com.kuma.cloud.blog.service.ChatBlacklistService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ChatBlacklistServiceImpl implements ChatBlacklistService {

    private final ChatBlacklistMapper blacklistMapper;

    /** 内存集合，启动时从 DB 加载，后续增删同步维护，O(1) 查询 */
    private final Set<String> blockedEmails = ConcurrentHashMap.newKeySet();

    @PostConstruct
    void init() {
        blacklistMapper.selectList(null)
                .forEach(b -> blockedEmails.add(b.getEmail().toLowerCase()));
    }

    @Override
    public void add(String email, String reason) {
        String normalized = email.strip().toLowerCase();
        ChatBlacklist record = new ChatBlacklist();
        record.setEmail(normalized);
        record.setReason(reason);
        record.setCreateTime(LocalDateTime.now());
        blacklistMapper.insert(record);
        blockedEmails.add(normalized);
    }

    @Override
    public void remove(Long id) {
        ChatBlacklist record = blacklistMapper.selectById(id);
        if (record != null) {
            blacklistMapper.deleteById(id);
            blockedEmails.remove(record.getEmail().toLowerCase());
        }
    }

    @Override
    public List<ChatBlacklist> list() {
        return blacklistMapper.selectList(new QueryWrapper<ChatBlacklist>().orderByDesc("create_time"));
    }

    @Override
    public boolean isBlocked(String email) {
        if (email == null || email.isBlank()) return false;
        return blockedEmails.contains(email.strip().toLowerCase());
    }
}
