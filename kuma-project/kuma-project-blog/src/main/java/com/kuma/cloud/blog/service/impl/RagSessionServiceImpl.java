package com.kuma.cloud.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kuma.boot.ai.service.AiRagService;
import com.kuma.cloud.blog.domain.entity.RagSession;
import com.kuma.cloud.blog.domain.vo.RagSessionVO;
import com.kuma.cloud.blog.mapper.RagSessionMapper;
import com.kuma.cloud.blog.security.BlogUserDetails;
import com.kuma.cloud.blog.service.RagSessionService;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RagSessionServiceImpl implements RagSessionService {

    private final RagSessionMapper ragSessionMapper;
    private final AiRagService aiRagService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RagSessionVO create(String title) {
        RagSession session = new RagSession();
        session.setId(UUID.randomUUID().toString());
        session.setUserId(currentUserId());
        session.setTitle(title != null && !title.isBlank() ? title : "新对话");
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        ragSessionMapper.insert(session);
        return toVO(session, false);
    }

    @Override
    public List<RagSessionVO> listAll() {
        Set<String> activeIds = aiRagService.listSessions();
        return ragSessionMapper.selectList(
                new LambdaQueryWrapper<RagSession>().orderByDesc(RagSession::getUpdatedAt)
        ).stream().map(s -> toVO(s, activeIds.contains(s.getId()))).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rename(String sessionId, String title) {
        RagSession update = new RagSession();
        update.setId(sessionId);
        update.setTitle(title);
        update.setUpdatedAt(LocalDateTime.now());
        ragSessionMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String sessionId) {
        ragSessionMapper.deleteById(sessionId);
        aiRagService.clearMemory(sessionId);
    }

    @Override
    public void ensureExists(String sessionId) {
        if (ragSessionMapper.selectById(sessionId) == null) {
            RagSession session = new RagSession();
            session.setId(sessionId);
            session.setUserId(currentUserId());
            session.setTitle("新对话");
            session.setCreatedAt(LocalDateTime.now());
            session.setUpdatedAt(LocalDateTime.now());
            ragSessionMapper.insert(session);
        }
    }

    private static Long currentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ((BlogUserDetails) principal).getLoginResponse().getUserId();
    }

    private static RagSessionVO toVO(RagSession s, boolean active) {
        RagSessionVO vo = new RagSessionVO();
        vo.setId(s.getId());
        vo.setTitle(s.getTitle());
        vo.setActive(active);
        vo.setCreatedAt(s.getCreatedAt());
        vo.setUpdatedAt(s.getUpdatedAt());
        return vo;
    }
}
