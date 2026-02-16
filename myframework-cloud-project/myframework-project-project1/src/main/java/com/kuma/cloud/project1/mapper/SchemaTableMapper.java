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

package com.kuma.cloud.project1.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kuma.cloud.project1.model.TableRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Schema 表 Mapper - 查询 blog_source 库（需配合 SchemaContext 切换 schema）
 *
 * @author kuma
 */
@Mapper
public interface SchemaTableMapper {

    /**
     * 查询指定 schema 下的表名列表（从 information_schema）
     */
    List<String> listTables(@Param("schemaName") String schemaName);

    /**
     * 分页查询指定表数据（表名、排序需在调用前校验，防止 SQL 注入）
     */
    IPage<TableRow> selectTablePage(IPage<TableRow> page,
                                   @Param("tableName") String tableName,
                                   @Param("sort") String sort,
                                   @Param("order") String order);
}
