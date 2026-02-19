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

package com.kuma.boot.data.datasource.druid;

import com.alibaba.druid.DbType;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 打印可执行的 sql 日志
 *
 * <p>参考：https://jfinal.com/share/2204
 */
public class SqlLogFilter extends FilterEventAdapter {

    private static final SQLUtils.FormatOption FORMAT_OPTION =
            new SQLUtils.FormatOption(false, false);

    @Override
    protected void statementExecuteBefore(StatementProxy statement, String sql) {
        statement.setLastExecuteStartNano();
    }

    @Override
    protected void statementExecuteBatchBefore(StatementProxy statement) {
        statement.setLastExecuteStartNano();
    }

    @Override
    protected void statementExecuteUpdateBefore(StatementProxy statement, String sql) {
        statement.setLastExecuteStartNano();
    }

    @Override
    protected void statementExecuteQueryBefore(StatementProxy statement, String sql) {
        statement.setLastExecuteStartNano();
    }

    @Override
    protected void statementExecuteAfter(
            StatementProxy statement, String sql, boolean firstResult) {
        statement.setLastExecuteTimeNano();
    }

    @Override
    protected void statementExecuteBatchAfter(StatementProxy statement, int[] result) {
        statement.setLastExecuteTimeNano();
    }

    @Override
    protected void statementExecuteQueryAfter(
            StatementProxy statement, String sql, ResultSetProxy resultSet) {
        statement.setLastExecuteTimeNano();
    }

    @Override
    protected void statementExecuteUpdateAfter(
            StatementProxy statement, String sql, int updateCount) {
        statement.setLastExecuteTimeNano();
    }

    @Override
    public void statement_close(FilterChain chain, StatementProxy statement) throws SQLException {
        // 支持动态开启
        // if (!properties.isSql()) {
        // 	return;
        // }

        // 是否开启调试
        if (!LogUtils.isInfoEnabled()) {
            return;
        }
        // 打印可执行的 sql
        String sql = statement.getBatchSql();
        // sql 为空直接返回
        if (StringUtils.isEmpty(sql)) {
            return;
        }
        int parametersSize = statement.getParametersSize();
        List<Object> parameters = new ArrayList<>(parametersSize);
        for (int i = 0; i < parametersSize; ++i) {
            JdbcParameter jdbcParam = statement.getParameter(i);
            parameters.add(jdbcParam != null ? jdbcParam.getValue() : null);
        }
        String dbType = statement.getConnectionProxy().getDirectDataSource().getDbType();
        String formattedSql = SQLUtils.format(sql, DbType.of(dbType), parameters, FORMAT_OPTION);
        printSql(formattedSql, statement);
    }

    private static void printSql(String sql, StatementProxy statement) {
        // 打印 sql
        String sqlLogger =
                """
            ==============  Sql Start  ==============
            Execute SQL ：{}
            Execute Time：{}
            ==============  Sql  End   ==============
            """;
        LogUtils.info(sqlLogger, sql.trim(), statement.getLastExecuteTimeNano());
    }
}
