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

package com.kuma.boot.data.datasource.init;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.data.datasource.utils.Jdbcs;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.List;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.util.Assert;


/**
 * StandardDatabaseScript
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public abstract class StandardDatabaseScript {

    private static final Logger logger = LoggerFactory.getLogger(StandardDatabaseScript.class);

    private static final String EVALUATE_SQL_PATTERN = "SELECT count(*) FROM %s";

    /**
     * 数据库脚本路径，第一个%S为组件名，第二个%S为数据库名，第三个为脚本文件名
     */
    private static final String INIT_SQL_PATTERN = "META-INF/database/%s/%s/%s.sql";

    public void setDataSource( DataSource dataSource ) {
        this.dataSource = dataSource;
    }

    private DataSource dataSource;

    public void execute() {
        try {
            String componentName = getComponentName();
            String evaluateTable = getEvaluateTable();

            Assert.notNull(componentName, "组件名不能为空");
            Assert.notNull(evaluateTable, "组件验证表名不能为空");

            String evaluateSql = String.format(EVALUATE_SQL_PATTERN, evaluateTable);

            Connection connection = null;
            Jdbcs.DbType databaseType = Jdbcs.DbType.MYSQL;

            try {
                connection = dataSource.getConnection();

                databaseType = Jdbcs.getDbType(connection.getMetaData().getURL());

                ScriptUtils.executeSqlScript(
                        connection, new ByteArrayResource(evaluateSql.getBytes()));
            } catch (DataAccessException e) {
                Throwable throwable = Throwables.getRootCause(e);
                String msg = throwable.getMessage();
                if (throwable
                        .getClass()
                        .getName()
                        .endsWith(SQLSyntaxErrorException.class.getSimpleName())
                        && ( msg.endsWith("doesn't exist") || msg.contains("not found") )) {
                    List<String> files = getInitSqlFile();
                    List<String> abFiles = Lists.newArrayListWithCapacity(files.size());
                    for (String file : files) {
                        String initSql =
                                String.format(
                                        INIT_SQL_PATTERN,
                                        componentName,
                                        databaseType.name().toLowerCase(),
                                        file);
                        abFiles.add(initSql);
                    }
                    if (connection != null) {
                        Connection conn = connection;
                        abFiles.forEach(sqlPath -> exeSqlFile(componentName, conn, sqlPath));
                    }
                }
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new BusinessException(e);
        }
    }

    public abstract String getEvaluateTable();

    public abstract String getComponentName();

    public abstract List<String> getInitSqlFile();

    /**
     * 执行初始化SQL时，发生错误是否忽略并继续。
     */
    protected boolean isContinueOnError() {
        return false;
    }

    private void exeSqlFile( String componentName, Connection connection, String sqlPath ) {
        logger.info("发现组件或模块［{}］基础数据还没有初始化，开始初始化：{}", componentName, sqlPath);
        try {
            Resource scriptResource =
                    ContextUtils.getApplicationContext().getResource("classpath:" + sqlPath);

            EncodedResource encodedResource = new EncodedResource(scriptResource, StandardCharsets.UTF_8);

            ScriptUtils.executeSqlScript(
                    connection, encodedResource, isContinueOnError(), false, "--", ";", "/*", "*/");
        } catch (Exception e) {
            throw new BusinessException("初始化" + componentName + "失败", e);
        }
    }
}
