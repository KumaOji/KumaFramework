/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.datasource.config;

/**
 * 数据源常用常量
 *
 * @author kuma
 */
public final class DataSourceConstants {

    private DataSourceConstants() {
    }

    /** MySQL 驱动类 */
    public static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";

    /** MySQL 默认端口 */
    public static final int MYSQL_DEFAULT_PORT = 3306;

    /** MySQL JDBC URL 前缀 */
    public static final String MYSQL_URL_PREFIX = "jdbc:mysql://";

    /** PostgreSQL 驱动类 */
    public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";

    /** PostgreSQL 默认端口 */
    public static final int POSTGRESQL_DEFAULT_PORT = 5432;

    /** PostgreSQL JDBC URL 前缀 */
    public static final String POSTGRESQL_URL_PREFIX = "jdbc:postgresql://";

    /** HikariCP 默认连接池大小 */
    public static final int DEFAULT_POOL_SIZE = 10;

    /** HikariCP 默认最小空闲连接数 */
    public static final int DEFAULT_MIN_IDLE = 5;

    /** HikariCP 默认连接超时(毫秒) */
    public static final long DEFAULT_CONNECTION_TIMEOUT = 30_000;

    /** HikariCP 默认空闲超时(毫秒) */
    public static final long DEFAULT_IDLE_TIMEOUT = 600_000;

    /** HikariCP 默认最大生命周期(毫秒) */
    public static final long DEFAULT_MAX_LIFETIME = 1_800_000;
}
