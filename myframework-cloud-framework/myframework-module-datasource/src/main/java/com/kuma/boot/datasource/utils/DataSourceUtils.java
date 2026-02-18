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

import com.kuma.boot.common.utils.sql.DbConn;
import com.kuma.boot.datasource.config.DataSourceConstants;

import javax.sql.DataSource;

/**
 * 数据源工具类
 *
 * @author kuma
 */
public final class DataSourceUtils {

    private DataSourceUtils() {
    }

    /**
     * 从 DataSource 创建 DbConn
     *
     * @param dataSource 数据源
     * @return DbConn 连接封装，需在 try-with-resources 中关闭
     */
    public static DbConn createDbConn(DataSource dataSource) {
        return new DbConn(dataSource);
    }

    /**
     * 创建 MySQL 数据库连接
     *
     * @param host     主机
     * @param port     端口，默认 3306
     * @param database 数据库名
     * @param user     用户名
     * @param password 密码
     * @return DbConn 连接封装，需在 try-with-resources 中关闭
     */
    public static DbConn createDbConnForMysql(
            String host, int port, String database, String user, String password) {
        return createDbConnForMysql(host, port, database, user, password, null);
    }

    /**
     * 创建 MySQL 数据库连接
     *
     * @param host     主机
     * @param port     端口，默认 3306
     * @param database 数据库名
     * @param user     用户名
     * @param password 密码
     * @param params   额外 URL 参数，如 useSSL=false&serverTimezone=Asia/Shanghai
     * @return DbConn 连接封装，需在 try-with-resources 中关闭
     */
    public static DbConn createDbConnForMysql(
            String host, int port, String database, String user, String password, String params) {
        String url = buildMysqlUrl(host, port, database, params);
        return new DbConn(url, user, password, DataSourceConstants.MYSQL_DRIVER);
    }

    /**
     * 创建 PostgreSQL 数据库连接
     *
     * @param host     主机
     * @param port     端口，默认 5432
     * @param database 数据库名
     * @param user     用户名
     * @param password 密码
     * @return DbConn 连接封装，需在 try-with-resources 中关闭
     */
    public static DbConn createDbConnForPostgresql(
            String host, int port, String database, String user, String password) {
        return createDbConnForPostgresql(host, port, database, user, password, null);
    }

    /**
     * 创建 PostgreSQL 数据库连接
     *
     * @param host     主机
     * @param port     端口，默认 5432
     * @param database 数据库名
     * @param user     用户名
     * @param password 密码
     * @param params   额外 URL 参数
     * @return DbConn 连接封装，需在 try-with-resources 中关闭
     */
    public static DbConn createDbConnForPostgresql(
            String host, int port, String database, String user, String password, String params) {
        String url = buildPostgresqlUrl(host, port, database, params);
        return new DbConn(url, user, password, DataSourceConstants.POSTGRESQL_DRIVER);
    }

    /**
     * 构建 MySQL JDBC URL
     *
     * @param host     主机
     * @param port     端口，默认 3306
     * @param database 数据库名
     * @return JDBC URL
     */
    public static String buildMysqlUrl(String host, int port, String database) {
        return buildMysqlUrl(host, port, database, null);
    }

    /**
     * 构建 MySQL JDBC URL
     *
     * @param host     主机
     * @param port     端口，默认 3306
     * @param database 数据库名
     * @param params   额外参数，如 useSSL=false&serverTimezone=Asia/Shanghai
     * @return JDBC URL
     */
    public static String buildMysqlUrl(String host, int port, String database, String params) {
        if (port <= 0) {
            port = DataSourceConstants.MYSQL_DEFAULT_PORT;
        }
        String url = DataSourceConstants.MYSQL_URL_PREFIX + host + ":" + port + "/" + database;
        if (params != null && !params.isBlank()) {
            url += (url.contains("?") ? "&" : "?") + params;
        }
        return url;
    }

    /**
     * 构建 PostgreSQL JDBC URL
     *
     * @param host     主机
     * @param port     端口，默认 5432
     * @param database 数据库名
     * @return JDBC URL
     */
    public static String buildPostgresqlUrl(String host, int port, String database) {
        return buildPostgresqlUrl(host, port, database, null);
    }

    /**
     * 构建 PostgreSQL JDBC URL
     *
     * @param host     主机
     * @param port     端口，默认 5432
     * @param database 数据库名
     * @param params   额外参数
     * @return JDBC URL
     */
    public static String buildPostgresqlUrl(String host, int port, String database, String params) {
        if (port <= 0) {
            port = DataSourceConstants.POSTGRESQL_DEFAULT_PORT;
        }
        String url = DataSourceConstants.POSTGRESQL_URL_PREFIX + host + ":" + port + "/" + database;
        if (params != null && !params.isBlank()) {
            url += (url.contains("?") ? "&" : "?") + params;
        }
        return url;
    }

    /**
     * 判断是否为 MySQL URL
     */
    public static boolean isMysqlUrl(String url) {
        return url != null && url.startsWith(DataSourceConstants.MYSQL_URL_PREFIX);
    }

    /**
     * 判断是否为 PostgreSQL URL
     */
    public static boolean isPostgresqlUrl(String url) {
        return url != null && url.startsWith(DataSourceConstants.POSTGRESQL_URL_PREFIX);
    }
}
