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

import com.mysql.cj.CharsetMapping;
import com.kuma.boot.common.utils.log.LogUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * 数据库工具类
 *
 */
public class DbUtils {

    private DbUtils() {}

    /**
     * 创建数据库（如果不存在）
     *
     * @param dbName     数据库名
     * @param dataSource
     * @return
     */
    public static boolean createDbIfAbsent(String dbName, DataSource dataSource) {
        return createDbIfAbsent(dbName, CharsetMapping.MYSQL_CHARSET_NAME_utf8, dataSource);
    }

    /**
     * 创建数据库（如果不存在）
     *
     * @param dbName     数据库名
     * @param charset    字符集
     * @param dataSource
     * @return
     * @see CharsetMapping
     */
    public static boolean createDbIfAbsent(String dbName, String charset, DataSource dataSource) {
        boolean result = false;
        String createDbSql =
                String.format("CREATE DATABASE IF NOT EXISTS %s CHARACTER SET %s", dbName, charset);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstat = connection.prepareStatement(createDbSql)) {
            pstat.executeUpdate();
            result = true;
        } catch (SQLException e) {
            LogUtils.error(e.getMessage(), e);
        } finally {
            LogUtils.warn("execute sql==>{}", createDbSql);
        }
        return result;
    }
}
