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

import com.kuma.boot.datasource.config.DataSourceConstants;

/**
 * 数据源工具类
 *
 * @author kuma
 */
public final class DataSourceUtils {

    private DataSourceUtils() {
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
