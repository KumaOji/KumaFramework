package com.kuma.cloud.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.cloud.blog.domain.entity.ReadyItem;
import com.kuma.cloud.blog.domain.query.ReadyQuery;
import com.kuma.cloud.blog.domain.vo.ReadyVO;
import com.kuma.cloud.blog.mapper.ReadyMapper;
import com.kuma.cloud.blog.service.ReadyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReadyServiceImpl implements ReadyService {

    private final ReadyMapper readyMapper;

    @Override
    public Long createReady(ReadyItem item) {
        LocalDateTime now = LocalDateTime.now();
        item.setCreateTime(now);
        item.setUpdateTime(now);
        if (item.getStatus() == null) item.setStatus(0);
        if (item.getPriority() == null) item.setPriority(0);
        readyMapper.insert(item);
        return item.getId();
    }

    @Override
    public boolean updateReady(ReadyItem item) {
        item.setUpdateTime(LocalDateTime.now());
        return readyMapper.updateById(item) > 0;
    }

    @Override
    public boolean deleteReady(Long id) {
        return readyMapper.deleteByIdLogic(id) > 0;
    }

    @Override
    public boolean deleteReadyPhysical(Long id) {
        return readyMapper.deleteByIdPhysical(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleStatus(Long id) {
        ReadyItem item = readyMapper.selectById(id);
        if (item == null) return false;
        int nextStatus = switch (item.getStatus()) {
            case 0 -> 1;
            case 1 -> 2;
            case 2 -> 0;
            default -> 0;
        };
        item.setStatus(nextStatus);
        item.setUpdateTime(LocalDateTime.now());
        return readyMapper.updateById(item) > 0;
    }

    @Override
    public ReadyVO getReadyById(Long id) {
        ReadyItem item = readyMapper.selectById(id);
        if (item == null) return null;
        ReadyVO vo = new ReadyVO();
        BeanUtils.copyProperties(item, vo);
        return vo;
    }

    @Override
    public IPage<ReadyVO> getReadyList(PageQuery pageQuery, ReadyQuery queryVO) {
        QueryWrapper<ReadyItem> qw = new QueryWrapper<>();
        qw.ne("status", 3);
        if (queryVO != null) {
            if (StringUtils.isNotEmpty(queryVO.getTitle())) qw.like("title", queryVO.getTitle());
            if (queryVO.getStatus() != null) qw.eq("status", queryVO.getStatus());
            if (queryVO.getPriority() != null) qw.eq("priority", queryVO.getPriority());
        }
        qw.orderByDesc("priority").orderByDesc("create_time");
        IPage<ReadyItem> page = readyMapper.selectPage(
                new Page<>(pageQuery.getCurrentPage(), pageQuery.getPageSize()), qw);
        return page.convert(item -> {
            ReadyVO vo = new ReadyVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        });
    }
}
