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

package com.kuma.boot.data.datasource.autoconfigure;

import com.kuma.boot.data.datasource.autoconfigure.properties.DataSourceProperties;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.jdbc.metadata.HikariDataSourcePoolMetadata;
import org.springframework.context.annotation.Bean;

/**
 * 健康检查
 */
@AutoConfiguration
@EnableConfigurationProperties(DataSourceProperties.class)
@ConditionalOnProperty(
        prefix = DataSourceProperties.PREFIX,
        name = "healthEnabled",
        havingValue = "true")
public class DataSourceHealthAutoConfiguration {
    /**
     * 解決新版Spring中,健康健康檢查用到 sharding jdbc 時,該元件沒有完全實現Mysql驅動導致的問題.
     */
    @Bean
    DataSourcePoolMetadataProvider dataSourcePoolMetadataProvider() {
        return dataSource ->
                dataSource instanceof HikariDataSource
                        // 這裡如果所使用的資料來源沒有對應的 DataSourcePoolMetadata 實現的話也可以全部使用
                        // NotAvailableDataSourcePoolMetadata
                        ? new HikariDataSourcePoolMetadata((HikariDataSource) dataSource)
                        : new NotAvailableDataSourcePoolMetadata();
    }

    /**
     * 不可用的資料來源池元資料.
     */
    private static class NotAvailableDataSourcePoolMetadata implements DataSourcePoolMetadata {
        @Override
        public Float getUsage() {
            return null;
        }

        @Override
        public Integer getActive() {
            return null;
        }

        @Override
        public Integer getMax() {
            return null;
        }

        @Override
        public Integer getMin() {
            return null;
        }

        @Override
        public String getValidationQuery() {
            // 該字串是適用於MySQL的簡單查詢語句,用於檢查檢查,其他資料庫可能需要更換
            return "select 1";
        }

        @Override
        public Boolean getDefaultAutoCommit() {
            return null;
        }
    }
}
