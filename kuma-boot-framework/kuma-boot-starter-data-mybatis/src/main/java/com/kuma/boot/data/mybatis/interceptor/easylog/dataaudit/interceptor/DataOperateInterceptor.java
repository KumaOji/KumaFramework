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

package com.kuma.boot.data.mybatis.interceptor.easylog.dataaudit.interceptor;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.TableNameParser;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import com.kuma.boot.data.mybatis.interceptor.easylog.common.threadlocal.DataOperateLogThreadLocal;
import com.kuma.boot.data.mybatis.interceptor.easylog.common.utils.audit.OperationDataChange;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

/** 数据更新拦截器 */
// @Component
// @Intercepts({
//	@Signature(
//		method = "query",
//		type = Executor.class,
//		args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
//	@Signature(
//		method = "query",
//		type = Executor.class,
//		args = {
//			MappedStatement.class,
//			Object.class,
//			RowBounds.class,
//			ResultHandler.class,
//			CacheKey.class,
//			BoundSql.class
//		}),
//	@Signature(
//		method = "queryCursor",
//		type = Executor.class,
//		args = {
//			MappedStatement.class,
//			Object.class,
//			RowBounds.class,
//		}),
//	@Signature(
//		method = "update",
//		type = Executor.class,
//		args = {MappedStatement.class, Object.class}),
//
//	@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class,
// Integer.class}),
//	@Signature(type = StatementHandler.class, method = "update", args = Statement.class),
//	@Signature(type = StatementHandler.class, method = "batch", args = Statement.class),
//	@Signature(type = StatementHandler.class, method = "query", args = {Statement.class,
// ResultHandler.class}),
//	@Signature(type = StatementHandler.class, method = "queryCursor", args = {Statement.class}),
//
//	@Signature(type = ParameterHandler.class, method = "getParameterObject", args = {}),
//
//	@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class}),
//	@Signature(type = ResultSetHandler.class, method = "handleCursorResultSets", args =
// {Statement.class})
// })
@Intercepts({
        @Signature(
                type = StatementHandler.class,
                method = "update",
                args = {Statement.class}),
        //	@Signature(type = StatementHandler.class, method = "delete", args = {Statement.class}),
        //	@Signature(type = StatementHandler.class, method = "insert", args = {Statement.class}),
        //	@Signature(type = StatementHandler.class, method = "batch", args = {Statement.class}),
})
public class DataOperateInterceptor extends JsqlParserSupport implements Interceptor {

    private final Logger logger = LoggerFactory.getLogger(DataOperateInterceptor.class);

    @Autowired private JdbcTemplate jdbcTemplate;

    @Override
    public Object intercept(Invocation invocation) throws Exception {
        try {
            // 判断是否需要记录日志
            if (DataOperateLogThreadLocal.DATA_CHANGES.get() == null) {
                return invocation.proceed();
            }

            Object firstArg = invocation.getArgs()[0];
            Statement statement = (Statement) firstArg;

            MetaObject stmtMetaObj = SystemMetaObject.forObject(statement);

            try {
                statement = (Statement) stmtMetaObj.getValue("stmt.statement");
            } catch (Exception e) {
            }

            if (stmtMetaObj.hasGetter("delegate")) {
                try {
                    statement = (Statement) stmtMetaObj.getValue("delegate");
                } catch (Exception ignored) {
                }
            }

            String originalSql = statement.toString();
            originalSql = originalSql.replaceAll("[\\s]+", StringPool.SPACE);
            int index = indexOfSqlStart(originalSql);
            if (index > 0) {
                originalSql = originalSql.substring(index);
            }

            StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
            PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(statementHandler);
            MappedStatement mappedStatement = mpSh.mappedStatement();

            if (mappedStatement.getSqlCommandType() != null) {
                // 更新数据
                if (SqlCommandType.UPDATE.equals(mappedStatement.getSqlCommandType())) {
                    // 获取执行Sql
                    String sql = originalSql.replace("where", "WHERE");
                    if (sql.endsWith("]")) {
                        sql = sql.replaceAll("]", "");
                    }

                    // 使用mybatis-plus 工具解析sql获取表名
                    Collection<String> tables = new TableNameParser(sql).tables();
                    if (CollectionUtils.isEmpty(tables)) {
                        return invocation.proceed();
                    }

                    String tableName = tables.iterator().next();

                    OperationDataChange change = new OperationDataChange();
                    change.setTableName(tableName);
                    change.setJdbcTemplate(jdbcTemplate);

                    // 设置sql用于执行完后查询新数据
                    String selectSql = sql.substring(sql.lastIndexOf("WHERE") + 5);
                    // 同表对同条数据操作多次只进行一次对比
                    if (DataOperateLogThreadLocal.DATA_CHANGES.get().stream()
                            .anyMatch(
                                    c ->
                                            tableName.equals(c.getTableName())
                                                    && selectSql.equals(c.getWhereSql()))) {
                        return invocation.proceed();
                    }
                    change.setWhereSql(selectSql);

                    // 获取请求时object
                    Object parameterObject =
                            statementHandler.getParameterHandler().getParameterObject();
                    change.setTransferData(Collections.singletonList(parameterObject));

                    String querySql = "select * from " + tableName + " where " + selectSql;
                    change.setQuerySql(querySql);

                    List<?> maps =
                            jdbcTemplate.query(
                                    querySql,
                                    new BeanPropertyRowMapper<>(parameterObject.getClass()));
                    change.setOldData(maps);
                    change.setEntityType(parameterObject.getClass());
                    change.setSqlCommandType(mappedStatement.getSqlCommandType());

                    DataOperateLogThreadLocal.DATA_CHANGES.get().add(change);
                }

                if (SqlCommandType.INSERT.equals(mappedStatement.getSqlCommandType())
                        || SqlCommandType.DELETE.equals(mappedStatement.getSqlCommandType())) {
                    OperationDataChange change = new OperationDataChange();
                    change.setSqlCommandType(mappedStatement.getSqlCommandType());
                    change.setQuerySql(originalSql);
                    DataOperateLogThreadLocal.DATA_CHANGES.get().add(change);
                }
            }
        } catch (Exception e) {
            logger.info("DataOperateInterceptor-intercept-error:{}", JSON.toJSONString(e));
        }
        return invocation.proceed();
    }

    /** 获取sql语句开头部分 */
    private int indexOfSqlStart(String sql) {
        String upperCaseSql = sql.toUpperCase();
        Set<Integer> set = new HashSet<>();
        set.add(upperCaseSql.indexOf("SELECT "));
        set.add(upperCaseSql.indexOf("UPDATE "));
        set.add(upperCaseSql.indexOf("INSERT "));
        set.add(upperCaseSql.indexOf("DELETE "));
        set.remove(-1);
        if (CollectionUtils.isEmpty(set)) {
            return -1;
        }
        List<Integer> list = new ArrayList<>(set);
        list.sort(Comparator.naturalOrder());
        return list.get(0);
    }
}
