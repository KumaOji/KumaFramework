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

package com.kuma.boot.datasource.utils;

import com.alibaba.fastjson2.JSONObject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * JDBC 工具类
 * 继承 common 模块的查询能力，并补充连接管理（close、execute）
 *
 * @author kuma
 */
public final class JdbcUtils {

    private JdbcUtils() {
    }

    // ==================== 连接管理 ====================

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

    // ==================== 查询方法（委托 common JdbcUtils） ====================

    /**
     * 数据源查询
     *
     * @param dataSource  数据源
     * @param preparedSql 预处理 SQL
     * @param params      参数
     * @return JSON 对象集合
     */
    public static List<JSONObject> selectList(
            DataSource dataSource, String preparedSql, Object... params) throws SQLException {
        return com.kuma.boot.common.utils.sql.JdbcUtils.selectList(dataSource, preparedSql, params);
    }

    /**
     * 数据源查询
     *
     * @param clazz       返回对象类型
     * @param dataSource  数据源
     * @param preparedSql 预处理 SQL
     * @param params      参数
     * @return Java 对象集合
     */
    public static <T> List<T> selectList(
            Class<T> clazz, DataSource dataSource, String preparedSql, Object... params)
            throws SQLException {
        return com.kuma.boot.common.utils.sql.JdbcUtils.selectList(clazz, dataSource, preparedSql, params);
    }

    /**
     * 数据源查询单条
     *
     * @param dataSource  数据源
     * @param preparedSql 预处理 SQL
     * @param params      参数
     * @return JSON 对象
     */
    public static JSONObject selectOne(DataSource dataSource, String preparedSql, Object... params)
            throws SQLException {
        return com.kuma.boot.common.utils.sql.JdbcUtils.selectOne(dataSource, preparedSql, params);
    }

    /**
     * 数据源查询单条
     *
     * @param clazz       返回对象类型
     * @param dataSource  数据源
     * @param preparedSql 预处理 SQL
     * @param params      参数
     * @return Java 对象
     */
    public static <T> T selectOne(
            Class<T> clazz, DataSource dataSource, String preparedSql, Object... params)
            throws SQLException {
        return com.kuma.boot.common.utils.sql.JdbcUtils.selectOne(clazz, dataSource, preparedSql, params);
    }

    /**
     * 连接查询
     *
     * @param connection  连接
     * @param preparedSql 预处理 SQL
     * @param params      参数
     * @return JSON 对象集合
     */
    public static List<JSONObject> selectList(
            Connection connection, String preparedSql, Object... params) throws SQLException {
        return com.kuma.boot.common.utils.sql.JdbcUtils.selectList(connection, preparedSql, params);
    }

    /**
     * 连接查询
     *
     * @param clazz       返回对象类型
     * @param connection  连接
     * @param preparedSql 预处理 SQL
     * @param params      参数
     * @return Java 对象集合
     */
    public static <T> List<T> selectList(
            Class<T> clazz, Connection connection, String preparedSql, Object... params)
            throws SQLException {
        return com.kuma.boot.common.utils.sql.JdbcUtils.selectList(clazz, connection, preparedSql, params);
    }

    /**
     * 连接查询单条
     *
     * @param connection  连接
     * @param preparedSql 预处理 SQL
     * @param params      参数
     * @return JSON 对象
     */
    public static JSONObject selectOne(Connection connection, String preparedSql, Object... params)
            throws SQLException {
        return com.kuma.boot.common.utils.sql.JdbcUtils.selectOne(connection, preparedSql, params);
    }

    /**
     * 连接查询单条
     *
     * @param clazz       返回对象类型
     * @param connection  连接
     * @param preparedSql 预处理 SQL
     * @param params      参数
     * @return Java 对象
     */
    public static <T> T selectOne(
            Class<T> clazz, Connection connection, String preparedSql, Object... params)
            throws SQLException {
        return com.kuma.boot.common.utils.sql.JdbcUtils.selectOne(clazz, connection, preparedSql, params);
    }

    // ==================== SQL 安全（委托 common SqlUtils） ====================

    /**
     * 校验 ORDER BY 字段，防止 SQL 注入；非法则抛异常
     *
     * @param value 排序字段，如 "id,desc" 或 "create_time ASC"
     * @return 原值
     */
    public static String escapeOrderBySql(String value) {
        return com.kuma.boot.common.utils.sql.SqlUtils.escapeOrderBySql(value);
    }

    /**
     * 验证 ORDER BY 语法是否符合规范（仅允许字母、数字、下划线、空格、逗号、小数点）
     */
    public static boolean isValidOrderBySql(String value) {
        return com.kuma.boot.common.utils.sql.SqlUtils.isValidOrderBySql(value);
    }

    /**
     * SQL 关键字检查，存在危险关键字则抛异常
     */
    public static void filterKeyword(String value) {
        com.kuma.boot.common.utils.sql.SqlUtils.filterKeyword(value);
    }
}
