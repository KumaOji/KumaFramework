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

package com.kuma.boot.data.datasource.utils;

import static com.kuma.boot.common.constant.StrPoolConstants.DOT;
import static com.kuma.boot.common.constant.StrPoolConstants.PERCENT;

import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/**
 * 数据库表工具类
 *
 */
public class DbTableUtils {

    private DbTableUtils() {}

    /**
     * 复制表结构
     *
     * @param sourceTableName 被复制的表名
     * @param targetTableName 复制后的表名
     * @param dataSource
     */
    public static boolean copyTableSchema(
            String sourceTableName, String targetTableName, DataSource dataSource) {
        boolean result = false;
        String copyTableSql =
                String.format("CREATE TABLE %s LIKE %s", targetTableName, sourceTableName);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstat = connection.prepareStatement(copyTableSql); ) {
            pstat.executeUpdate();
            result = true;
        } catch (SQLException e) {
            LogUtils.error(e.getMessage(), e);
        } finally {
            LogUtils.warn("execute sql==>{}", copyTableSql);
        }
        return result;
    }

    /**
     * 创建表（如果不存在）
     *
     * @param targetDbName    数据库名（传null，则表示不限制）
     * @param sourceTableName 源表
     * @param targetTableName 待创建的表
     * @param dataSource
     */
    public static boolean createTableIfAbsent(
            String targetDbName,
            String sourceTableName,
            String targetTableName,
            DataSource dataSource) {
        boolean result = true;
        try {
            boolean exist = existTable(targetDbName, targetTableName, dataSource);
            if (!exist) {
                if (targetDbName != null) {
                    targetTableName = targetDbName + DOT + targetTableName;
                }
                result = copyTableSchema(sourceTableName, targetTableName, dataSource);
            }
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
            result = false;
        }
        return result;
    }

    /**
     * 判断表是否已存在
     *
     * @param targetDbName 数据库名（传null，则表示不限制）
     * @param tableName    表名
     * @param dataSource
     * @return
     * @throws Exception
     */
    public static boolean existTable(String targetDbName, String tableName, DataSource dataSource) {
        List<String> tables = queryTables(targetDbName, tableName, false, dataSource);
        return tables.contains(tableName);
    }

    /**
     * 通过表名前缀查询所有的表名
     *
     * @param targetDbName    数据库名（传null，则表示不限制）
     * @param tableNamePrefix 表名前缀
     * @param dataSource
     * @return
     */
    public static List<String> queryTablesByPrefix(
            String targetDbName, String tableNamePrefix, DataSource dataSource) {
        return queryTables(targetDbName, tableNamePrefix, true, dataSource);
    }

    /**
     * 根据表名查询满足条件的表
     *
     * @param targetDbName 数据库名（传null，则表示不限制）
     * @param tableName    表名
     * @param prefix       是否表名前缀匹配
     * @param dataSource
     * @return
     */
    private static List<String> queryTables(
            String targetDbName, String tableName, boolean prefix, DataSource dataSource) {
        if (prefix) {
            tableName += PERCENT;
        }
        List<String> tablesWithPrefix = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(targetDbName, null, tableName, null);
            while (resultSet.next()) {
                tablesWithPrefix.add(resultSet.getString(3));
            }
        } catch (SQLException e) {
            LogUtils.error(e.getMessage(), e);
        } finally {
            LogUtils.debug(
                    "queryTables|tableName={}, result={}",
                    tableName,
                    JacksonUtils.toJson(tablesWithPrefix));
        }
        return tablesWithPrefix;
    }
}
