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

package com.kuma.boot.data.mybatis.sharding.config;

/**
 * 数据库数据源配置， 支持多数据源 如果需要其他属性，可以在此添加，然后在 HikariCpFactory 中设置相应属性即可
 *
 */
public class DataSourceProps {

    private HikariProps[] list;

    /**
     * HikariProps
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public static class HikariProps {

        /**
         * name 是作为map的KEY使用需要保证唯一性
         */
        private String name;

        private long connectionTimeout = 2000;
        private long idleTimeout = 2000;
        private String connectionTestQuery = "SELECT 1";
        private int maximumPoolSize = 10;
        private String driverClassName;
        private String jdbcUrl;
        private String username;
        private String password;

        public String getName() {
            return name;
        }

        public void setName( String name ) {
            this.name = name;
        }

        public long getConnectionTimeout() {
            return connectionTimeout;
        }

        public void setConnectionTimeout( long connectionTimeout ) {
            this.connectionTimeout = connectionTimeout;
        }

        public long getIdleTimeout() {
            return idleTimeout;
        }

        public void setIdleTimeout( long idleTimeout ) {
            this.idleTimeout = idleTimeout;
        }

        public String getConnectionTestQuery() {
            return connectionTestQuery;
        }

        public void setConnectionTestQuery( String connectionTestQuery ) {
            this.connectionTestQuery = connectionTestQuery;
        }

        public int getMaximumPoolSize() {
            return maximumPoolSize;
        }

        public void setMaximumPoolSize( int maximumPoolSize ) {
            this.maximumPoolSize = maximumPoolSize;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName( String driverClassName ) {
            this.driverClassName = driverClassName;
        }

        public String getJdbcUrl() {
            return jdbcUrl;
        }

        public void setJdbcUrl( String jdbcUrl ) {
            this.jdbcUrl = jdbcUrl;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername( String username ) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword( String password ) {
            this.password = password;
        }
    }

    public HikariProps[] getList() {
        return list;
    }

    public void setList( HikariProps[] list ) {
        this.list = list;
    }
}
