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

package com.kuma.boot.data.datasource.multiple.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ExtDataSourceProperties
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@ConfigurationProperties(prefix = MultipleDataSourceProperties.PREFIX, ignoreUnknownFields = false)
public class MultipleDataSourceProperties {

    public static final String PREFIX = "kuma.boot.data.datasource.multiple";

    private boolean enabled;

    private Mysql mysql;
    private ClickHouse clickhouse;
    private Doris doris;
    private Hive hive;
    private Tidb tidb;
    private Trino trino;

    public static class Mysql {

        private boolean enabled = false;
        // com.mysql.cj.jdbc.Driver
        private String driverClassName;
        // jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8
        private String url;
        // root
        private String username;
        // 123456
        private String password;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled( boolean enabled ) {
            this.enabled = enabled;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName( String driverClassName ) {
            this.driverClassName = driverClassName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl( String url ) {
            this.url = url;
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

    public static class Trino {

        private boolean enabled = false;
        // io.trino.jdbc.TrinoDriver
        private String driverClassName;
        // jdbc:trino://127.0.0.1:49228/es
        private String url;
        // xx
        private String username;
        // xx
        private String password;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled( boolean enabled ) {
            this.enabled = enabled;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName( String driverClassName ) {
            this.driverClassName = driverClassName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl( String url ) {
            this.url = url;
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

    public static class Tidb {

        private boolean enabled = false;

        // com.mysql.cj.jdbc.Driver
        private String driverClassName;
        // jdbc:mysql://192.168.1.104:4000/test_one?useUnicode=true&characterEncoding=utf-8&&useOldAliasMetadataBehavior=true&useSSL=false
        private String url;
        // root
        private String username;
        // root
        private String password;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled( boolean enabled ) {
            this.enabled = enabled;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName( String driverClassName ) {
            this.driverClassName = driverClassName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl( String url ) {
            this.url = url;
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

    public static class Hive {

        private boolean enabled = false;

        // org.apache.hive.jdbc.HiveDriver
        private String driverClassName;
        // jdbc:hive2://hadoop:10000/default
        private String url;
        // abc
        private String username;
        // abc
        private String password;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled( boolean enabled ) {
            this.enabled = enabled;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName( String driverClassName ) {
            this.driverClassName = driverClassName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl( String url ) {
            this.url = url;
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

    public static class Doris {

        private boolean enabled = false;

        // com.mysql.cj.jdbc.Driver
        private String driverClassName;
        // jdbc:mysql://192.168.1.161:9030/wudl_db?characterEncoding=utf-8&useSSL=false
        private String url;
        // root
        private String username;
        // root
        private String password;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled( boolean enabled ) {
            this.enabled = enabled;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName( String driverClassName ) {
            this.driverClassName = driverClassName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl( String url ) {
            this.url = url;
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

    public static class ClickHouse {

        private boolean enabled = false;
        // com.clickhouse.jdbc.ClickHouseDriver
        private String driverClassName;
        // jdbc:clickhouse://127.0.0.1:8123/test
        private String url;
        // admin
        private String username;
        // 123456
        private String password;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled( boolean enabled ) {
            this.enabled = enabled;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName( String driverClassName ) {
            this.driverClassName = driverClassName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl( String url ) {
            this.url = url;
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

    public Mysql getMysql() {
        return mysql;
    }

    public void setMysql( Mysql mysql ) {
        this.mysql = mysql;
    }

    public ClickHouse getClickhouse() {
        return clickhouse;
    }

    public void setClickhouse( ClickHouse clickhouse ) {
        this.clickhouse = clickhouse;
    }

    public Doris getDoris() {
        return doris;
    }

    public void setDoris( Doris doris ) {
        this.doris = doris;
    }

    public Hive getHive() {
        return hive;
    }

    public void setHive( Hive hive ) {
        this.hive = hive;
    }

    public Tidb getTidb() {
        return tidb;
    }

    public void setTidb( Tidb tidb ) {
        this.tidb = tidb;
    }

    public Trino getTrino() {
        return trino;
    }

    public void setTrino( Trino trino ) {
        this.trino = trino;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled( boolean enabled ) {
        this.enabled = enabled;
    }
}
