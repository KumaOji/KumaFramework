package com.kuma.cloud.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.cloud.blog.domain.entity.FriendLink;
import com.kuma.cloud.blog.domain.query.FriendLinkQuery;
import com.kuma.cloud.blog.domain.vo.FriendLinkVO;
import com.kuma.cloud.blog.mapper.FriendLinkMapper;
import com.kuma.cloud.blog.service.FriendLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendLinkServiceImpl implements FriendLinkService {

    private final FriendLinkMapper friendLinkMapper;

    @Override
    public Long apply(FriendLink friendLink) {
        LocalDateTime now = LocalDateTime.now();
        friendLink.setCreateTime(now);
        friendLink.setUpdateTime(now);
        friendLink.setStatus(0);
        friendLink.setViewCount(0);
        if (friendLink.getSortOrder() == null) friendLink.setSortOrder(0);
        friendLinkMapper.insert(friendLink);
        return friendLink.getId();
    }

    @Override
    public Long create(FriendLink friendLink) {
        LocalDateTime now = LocalDateTime.now();
        friendLink.setCreateTime(now);
        friendLink.setUpdateTime(now);
        if (friendLink.getStatus() == null) friendLink.setStatus(1);
        friendLink.setViewCount(0);
        if (friendLink.getSortOrder() == null) friendLink.setSortOrder(0);
        friendLinkMapper.insert(friendLink);
        return friendLink.getId();
    }

    @Override
    public boolean update(FriendLink friendLink) {
        friendLink.setUpdateTime(LocalDateTime.now());
        return friendLinkMapper.updateById(friendLink) > 0;
    }

    @Override
    public boolean delete(Long id) {
        FriendLink fl = new FriendLink();
        fl.setId(id);
        fl.setStatus(2);
        fl.setUpdateTime(LocalDateTime.now());
        return friendLinkMapper.updateById(fl) > 0;
    }

    @Override
    public boolean approve(Long id) {
        FriendLink fl = new FriendLink();
        fl.setId(id);
        fl.setStatus(1);
        fl.setUpdateTime(LocalDateTime.now());
        return friendLinkMapper.updateById(fl) > 0;
    }

    @Override
    public boolean incrementViewCount(Long id) {
        return friendLinkMapper.incrementViewCount(id) > 0;
    }

    @Override
    public List<FriendLinkVO> getApprovedList() {
        QueryWrapper<FriendLink> qw = new QueryWrapper<>();
        qw.eq("status", 1).orderByAsc("sort_order").orderByDesc("create_time");
        return friendLinkMapper.selectList(qw).stream().map(this::toVO).toList();
    }

    @Override
    public IPage<FriendLinkVO> adminList(PageQuery pageQuery, FriendLinkQuery queryVO) {
        QueryWrapper<FriendLink> qw = new QueryWrapper<>();
        if (queryVO != null) {
            if (StringUtils.isNotEmpty(queryVO.getName())) qw.like("name", queryVO.getName());
            if (StringUtils.isNotEmpty(queryVO.getCategory())) qw.eq("category", queryVO.getCategory());
            if (queryVO.getStatus() != null) qw.eq("status", queryVO.getStatus());
        }
        qw.orderByAsc("sort_order").orderByDesc("create_time");

        int current = pageQuery != null && pageQuery.getCurrentPage() != null ? pageQuery.getCurrentPage() : 1;
        int size = pageQuery != null && pageQuery.getPageSize() != null ? pageQuery.getPageSize() : 20;

        IPage<FriendLink> page = friendLinkMapper.selectPage(new Page<>(current, size), qw);
        return page.convert(this::toVO);
    }

    private FriendLinkVO toVO(FriendLink fl) {
        FriendLinkVO vo = new FriendLinkVO();
        BeanUtils.copyProperties(fl, vo);
        return vo;
    }
}
