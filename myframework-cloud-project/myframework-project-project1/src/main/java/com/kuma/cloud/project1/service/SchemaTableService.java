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

package com.kuma.cloud.project1.service;

import com.kuma.boot.common.model.result.PageResult;
import com.kuma.cloud.project1.request.TablePageQuery;

import java.util.List;
import java.util.Map;

/**
 * Schema 表服务 - 查看 blog_source 库表及分页查询表数据
 *
 * @author kuma
 */
public interface SchemaTableService {

    /**
     * 获取 blog_source 库下的所有表名
     */
    List<String> listTables();

    /**
     * 分页查询指定表的数据
     *
     * @param query 表名 + 分页参数
     * @return 分页结果，data 为 List&lt;Map&lt;列名, 值&gt;&gt;
     */
    PageResult<Map<String, Object>> pageTableData(TablePageQuery query);
}
