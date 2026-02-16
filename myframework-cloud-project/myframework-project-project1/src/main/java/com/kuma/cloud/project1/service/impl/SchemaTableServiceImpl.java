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

import com.kuma.boot.common.model.result.PageResult;
import com.kuma.cloud.project1.request.TablePageQuery;
import com.kuma.cloud.project1.service.SchemaTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Schema 表服务实现 - 查询 blog_source 库
 *
 * @author kuma
 */
@Service
@RequiredArgsConstructor
public class SchemaTableServiceImpl implements SchemaTableService {

    private static final String SCHEMA_BLOG_SOURCE = "blog_source";

    private final DataSource dataSource;

    @Override
    public List<String> listTables() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        String sql = "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = ? ORDER BY TABLE_NAME";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, SCHEMA_BLOG_SOURCE);
        List<String> tables = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Object name = row.get("TABLE_NAME");
            if (name != null) {
                tables.add(name.toString());
            }
        }
        return tables;
    }

    @Override
    public PageResult<Map<String, Object>> pageTableData(TablePageQuery query) {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        String tableName = query.getTableName().trim();
        if (!tableName.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("表名格式非法");
        }
        int current = query.getCurrentPage() != null ? query.getCurrentPage() : 1;
        int size = query.getPageSize() != null ? query.getPageSize() : 10;
        long offset = (current - 1L) * size;

        String orderClause = "";
        if (StringUtils.hasText(query.getSort()) && query.getSort().matches("^[a-zA-Z0-9_]+$")) {
            String order = "asc".equalsIgnoreCase(query.getOrder()) ? "ASC" : "DESC";
            orderClause = " ORDER BY `" + query.getSort().replace("`", "``") + "` " + order;
        }

        String qualifiedTable = SCHEMA_BLOG_SOURCE + ".`" + tableName.replace("`", "``") + "`";
        String countSql = "SELECT COUNT(*) FROM " + qualifiedTable;
        Long total = jdbc.queryForObject(countSql, Long.class);
        if (total == null) total = 0L;

        String dataSql = "SELECT * FROM " + qualifiedTable + orderClause + " LIMIT ? OFFSET ?";
        List<Map<String, Object>> data = jdbc.queryForList(dataSql, size, offset);

        int totalPage = (int) ((total + size - 1) / size);
        return PageResult.of(total, totalPage, current, size, data);
    }
}
