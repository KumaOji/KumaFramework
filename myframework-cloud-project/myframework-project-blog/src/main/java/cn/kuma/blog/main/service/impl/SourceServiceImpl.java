package cn.kuma.blog.main.service.impl;

import cn.kuma.blog.common.model.domain.PageParam;
import cn.kuma.blog.common.model.domain.PageResult;
import cn.kuma.blog.framework.mybatisplus.toolkit.PageUtil;
import cn.kuma.blog.framework.mybatisplus.util.SchemaContext;
import cn.kuma.blog.main.domain.entity.Source;
import cn.kuma.blog.main.mapper.SourceMapper;
import cn.kuma.blog.main.service.SourceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 资源服务实现类
 *
 * @author Kuma
 * @version 1.0
 */
@Service
public class SourceServiceImpl implements SourceService {

    private static final String SCHEMA_NAME = "blog_source";

    @Autowired
    private SourceMapper sourceMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSource(Source source) {
        final Long[] result = new Long[1];
        SchemaContext.withSchema(SCHEMA_NAME, () -> {
            LocalDateTime now = LocalDateTime.now();
            source.setCreateTime(now);
            source.setUpdateTime(now);
            sourceMapper.insert(source);
            result[0] = source.getId();
        });
        return result[0];
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSource(Source source) {
        final boolean[] result = new boolean[1];
        SchemaContext.withSchema(SCHEMA_NAME, () -> {
            source.setUpdateTime(LocalDateTime.now());
            result[0] = sourceMapper.updateById(source) > 0;
        });
        return result[0];
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSource(Long id) {
        final boolean[] result = new boolean[1];
        SchemaContext.withSchema(SCHEMA_NAME, () -> {
            result[0] = sourceMapper.deleteById(id) > 0;
        });
        return result[0];
    }

    @Override
    public Source getSourceById(Long id) {
        final Source[] result = new Source[1];
        SchemaContext.withSchema(SCHEMA_NAME, () -> {
            result[0] = sourceMapper.selectById(id);
        });
        return result[0];
    }

    @Override
    public PageResult<Source> getSourceList(PageParam pageParam, String name) {
        final PageResult<Source>[] result = new PageResult[1];
        SchemaContext.withSchema(SCHEMA_NAME, () -> {
            QueryWrapper<Source> queryWrapper = new QueryWrapper<>();
            
            // 名称模糊查询
            if (StringUtils.isNotEmpty(name)) {
                queryWrapper.like("name", name);
            }
            
            // 按创建时间降序排序
            queryWrapper.orderByDesc("create_time");
            
            // 分页查询
            IPage<Source> sourcePage = sourceMapper.selectPage(PageUtil.prodPage(pageParam), queryWrapper);
            
            // 构建分页结果
            PageResult<Source> pageResult = new PageResult<>();
            pageResult.setRecords(sourcePage.getRecords());
            pageResult.setTotal(sourcePage.getTotal());
            
            result[0] = pageResult;
        });
        return result[0];
    }

    @Override
    public List<Source> getAllSources() {
        final List<Source>[] result = new List[1];
        SchemaContext.withSchema(SCHEMA_NAME, () -> {
            QueryWrapper<Source> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("create_time");
            result[0] = sourceMapper.selectList(queryWrapper);
        });
        return result[0] != null ? result[0] : new ArrayList<>();
    }
}
