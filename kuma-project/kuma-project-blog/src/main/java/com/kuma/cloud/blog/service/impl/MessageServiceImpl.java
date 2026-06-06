package com.kuma.cloud.blog.service.impl;

import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.cloud.blog.domain.entity.Message;
import com.kuma.cloud.blog.domain.vo.MessageQueryVO;
import com.kuma.cloud.blog.domain.vo.MessageVO;
import com.kuma.cloud.blog.mapper.MessageMapper;
import com.kuma.cloud.blog.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;

    @Override
    public Long postMessage(Message message, String ip) {
        LocalDateTime now = LocalDateTime.now();
        message.setCreateTime(now);
        message.setUpdateTime(now);
        message.setIp(ip);
        message.setLikeCount(0);
        message.setStatus(0);
        messageMapper.insert(message);
        return message.getId();
    }

    @Cached(name = "message:approved", cacheType = CacheType.LOCAL, expire = 120)
    @Override
    public List<MessageVO> getApprovedList() {
        QueryWrapper<Message> qw = new QueryWrapper<>();
        qw.eq("status", 1).isNull("parent_id").orderByDesc("create_time");
        List<Message> tops = messageMapper.selectList(qw);
        if (tops.isEmpty()) return new ArrayList<>();

        List<Long> topIds = tops.stream().map(Message::getId).toList();
        List<Message> replies = messageMapper.selectApprovedRepliesByParentIds(topIds);
        Map<Long, List<Message>> replyMap = replies.stream()
                .collect(Collectors.groupingBy(Message::getParentId));

        return tops.stream().map(m -> {
            MessageVO vo = toVO(m);
            List<Message> children = replyMap.getOrDefault(m.getId(), List.of());
            vo.setReplies(children.stream().map(this::toVO).toList());
            return vo;
        }).toList();
    }

    @Override
    public IPage<MessageVO> adminList(PageQuery pageQuery, MessageQueryVO queryVO) {
        QueryWrapper<Message> qw = new QueryWrapper<>();
        if (queryVO != null) {
            if (StringUtils.isNotEmpty(queryVO.getNickname())) qw.like("nickname", queryVO.getNickname());
            if (queryVO.getStatus() != null) qw.eq("status", queryVO.getStatus());
        }
        qw.orderByDesc("create_time");

        int current = pageQuery != null && pageQuery.getCurrentPage() != null ? pageQuery.getCurrentPage() : 1;
        int size = pageQuery != null && pageQuery.getPageSize() != null ? pageQuery.getPageSize() : 20;

        IPage<Message> page = messageMapper.selectPage(new Page<>(current, size), qw);
        return page.convert(this::toVO);
    }

    @CacheInvalidate(name = "message:approved")
    @Override
    public boolean approve(Long id) {
        Message msg = new Message();
        msg.setId(id);
        msg.setStatus(1);
        msg.setUpdateTime(LocalDateTime.now());
        return messageMapper.updateById(msg) > 0;
    }

    @CacheInvalidate(name = "message:approved")
    @Override
    public boolean delete(Long id) {
        Message msg = new Message();
        msg.setId(id);
        msg.setStatus(2);
        msg.setUpdateTime(LocalDateTime.now());
        return messageMapper.updateById(msg) > 0;
    }

    @Override
    public boolean incrementLike(Long id) {
        return messageMapper.incrementLikeCount(id) > 0;
    }

    @CacheInvalidate(name = "message:approved")
    @Override
    public boolean purge(Long id) {
        Message msg = messageMapper.selectById(id);
        if (msg == null || msg.getStatus() != 2) {
            throw new IllegalStateException("只能物理删除 status=2 的留言");
        }
        return messageMapper.deleteById(id) > 0;
    }

    private MessageVO toVO(Message m) {
        MessageVO vo = new MessageVO();
        BeanUtils.copyProperties(m, vo);
        return vo;
    }
}
