package com.kuma.cloud.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuma.boot.common.model.request.PageQuery;
import com.kuma.cloud.blog.domain.entity.Source;
import com.kuma.cloud.blog.mapper.SourceMapper;
import com.kuma.cloud.blog.service.SourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SourceServiceImpl implements SourceService {

    private final SourceMapper sourceMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSource(Source source) {
        LocalDateTime now = LocalDateTime.now();
        source.setCreateTime(now);
        source.setUpdateTime(now);
        sourceMapper.insert(source);
        return source.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSource(Source source) {
        source.setUpdateTime(LocalDateTime.now());
        return sourceMapper.updateById(source) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSource(Long id) {
        return sourceMapper.deleteById(id) > 0;
    }

    @Override
    public Source getSourceById(Long id) {
        return sourceMapper.selectById(id);
    }

    @Override
    public IPage<Source> getSourceList(PageQuery pageQuery) {
        return sourceMapper.selectPage(
                new Page<>(pageQuery.getCurrentPage(), pageQuery.getPageSize()),
                new LambdaQueryWrapper<Source>().orderByDesc(Source::getCreateTime));
    }

    @Override
    public List<Source> getAllSources() {
        return sourceMapper.selectList(new LambdaQueryWrapper<>());
    }
}
