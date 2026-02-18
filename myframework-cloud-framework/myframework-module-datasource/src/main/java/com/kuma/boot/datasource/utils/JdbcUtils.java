/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.datasource.utils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * JDBC 工具类
 *
 * @author kuma
 */
public final class JdbcUtils {

    private JdbcUtils() {
    }

    /**
     * 安全关闭连接
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }

    /**
     * 安全关闭 Statement
     */
    public static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ignored) {
            }
        }
    }

    /**
     * 安全关闭 ResultSet
     */
    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {
            }
        }
    }

    /**
     * 安全关闭 ResultSet、Statement、Connection
     */
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        close(rs);
        close(stmt);
        close(conn);
    }

    /**
     * 从 DataSource 获取连接并执行操作，自动关闭连接
     */
    public static <T> T execute(DataSource dataSource, ConnectionCallback<T> callback) throws SQLException {
        Connection conn = dataSource.getConnection();
        try {
            return callback.doInConnection(conn);
        } finally {
            close(conn);
        }
    }

    /**
     * 连接回调
     */
    @FunctionalInterface
    public interface ConnectionCallback<T> {
        T doInConnection(Connection conn) throws SQLException;
    }
}
