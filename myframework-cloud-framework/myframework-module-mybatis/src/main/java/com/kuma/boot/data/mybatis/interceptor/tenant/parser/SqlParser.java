/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.interceptor.tenant.parser;

import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;

/**
 * sql解析器接口
 *
 * @author wency_cai
 */
public interface SqlParser {

    /**
     * sql语句处理入口
     *
     * @param sql 语句处理入口
     * @return 返回串改后的sql语句
     */
    String setTenantParameter(String sql);

    /**
     * sql语句处理入口
     *
     * @param sql 语句处理入口
     * @return 返回串改后的sql语句
     */
    String setTenantParameter(String sql, Object paramTenantId);

    /**
     * 解析查询语句
     *
     * @param selectBody select语句处理
     */
    void processSelectBody(SQLSelectQuery selectBody, Object paramTenantId);

    /**
     * 解析新增语句
     *
     * @param insert 语句处理
     */
    void processInsert(SQLInsertStatement insert, Object paramTenantId);

    /**
     * 解析更新语句
     *
     * @param update 语句处理
     */
    void processUpdate(SQLUpdateStatement update, Object paramTenantId);

    /**
     * 解析删除语句
     *
     * @param delete 删除语句处理
     */
    void processDelete(SQLDeleteStatement delete, Object paramTenantId);
}
