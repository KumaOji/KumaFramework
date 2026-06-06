package com.kuma.cloud.blog.service;

import com.kuma.cloud.blog.domain.entity.ChatBlacklist;

import java.util.List;

public interface ChatBlacklistService {

    void add(String email, String reason);

    void remove(Long id);

    List<ChatBlacklist> list();

    boolean isBlocked(String email);
}
