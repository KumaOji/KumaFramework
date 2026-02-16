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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kuma.boot.common.model.result.PageResult;
import com.kuma.boot.datasource.context.SchemaContext;
import com.kuma.boot.mybatis.page.PageUtils;
import com.kuma.cloud.project1.mapper.SchemaTableMapper;
import com.kuma.cloud.project1.model.TableRow;
import com.kuma.cloud.project1.request.TablePageQuery;
import com.kuma.cloud.project1.service.SchemaTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Schema 表服务实现 - 使用 Schema 切换 + MyBatis-Plus 查询 blog_source 库
 *
 * @author kuma
 */
@Service
@RequiredArgsConstructor
public class SchemaTableServiceImpl implements SchemaTableService {

    private static final String SCHEMA_BLOG_SOURCE = "blog_source";

    private final SchemaTableMapper schemaTableMapper;

    @Override
    public List<String> listTables() {
        return SchemaContext.withSchema(SCHEMA_BLOG_SOURCE,
                () -> schemaTableMapper.listTables(SCHEMA_BLOG_SOURCE));
    }

    @Override
    public PageResult<Map<String, Object>> pageTableData(TablePageQuery query) {
        String tableName = query.getTableName().trim();
        if (!tableName.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("表名格式非法");
        }
        int current = query.getCurrentPage() != null ? query.getCurrentPage() : 1;
        int size = query.getPageSize() != null ? query.getPageSize() : 10;

        String sort = null;
        String order = "ASC";
        if (StringUtils.hasText(query.getSort()) && query.getSort().matches("^[a-zA-Z0-9_]+$")) {
            sort = query.getSort();
            order = "asc".equalsIgnoreCase(query.getOrder()) ? "ASC" : "DESC";
        }

        final String finalSort = sort;
        final String finalOrder = order;

        IPage<TableRow> page = SchemaContext.withSchema(SCHEMA_BLOG_SOURCE, () -> {
            Page<TableRow> p = new Page<>(current, size);
            return schemaTableMapper.selectTablePage(p, tableName, finalSort, finalOrder);
        });

        // TableRow extends Map，强转以满足 PageResult<Map<String, Object>> 接口
        @SuppressWarnings("unchecked")
        PageResult<Map<String, Object>> result = (PageResult<Map<String, Object>>) (PageResult<?>) PageUtils.toPageResult(page);
        return result;
    }
}
