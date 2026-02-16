/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.cloud.project1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuma.boot.common.model.domain.PageParam;
import com.kuma.boot.common.model.result.PageResult;
import com.kuma.boot.datasource.context.SchemaContext;
import com.kuma.boot.mybatis.page.PageUtils;
import com.kuma.cloud.project1.entity.Source;
import com.kuma.cloud.project1.mapper.SourceMapper;
import com.kuma.cloud.project1.service.SourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 资源服务实现 - 查询 blog_source 库的 source 表
 *
 * @author kuma
 */
@Service
@RequiredArgsConstructor
public class SourceServiceImpl implements SourceService {

    private static final String SCHEMA_BLOG_SOURCE = "blog_source";

    private final SourceMapper sourceMapper;

    @Override
    public PageResult<Source> pageList(PageParam pageParam, String name) {
        final PageResult<Source>[] result = new PageResult[1];
        SchemaContext.withSchema(SCHEMA_BLOG_SOURCE, () -> {
            Page<Source> page = PageUtils.toPage(pageParam);
            LambdaQueryWrapper<Source> wrapper = new LambdaQueryWrapper<>();
            if (StringUtils.hasText(name)) {
                wrapper.like(Source::getName, name);
            }
            wrapper.orderByDesc(Source::getCreateTime);
            IPage<Source> resultPage = sourceMapper.selectPage(page, wrapper);
            result[0] = PageUtils.toPageResult(resultPage);
        });
        return result[0];
    }

    @Override
    public Source getById(Long id) {
        final Source[] result = new Source[1];
        SchemaContext.withSchema(SCHEMA_BLOG_SOURCE, () -> {
            result[0] = sourceMapper.selectById(id);
        });
        return result[0];
    }
}
