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

package com.kuma.boot.data.datasource.multiple.ck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/**
 * ClickHouseJdbcBaseDaoImpl
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@Repository
@ConditionalOnProperty(name = "kuma.boot.data.datasource.multiple.clickhouse.enabled", havingValue = "true")
public class ClickHouseJdbcBaseDaoImpl {

    private JdbcTemplate jdbcTemplate;
    private JdbcClient jdbcClient;

    public JdbcClient getJdbcClient() {
        return jdbcClient;
    }

    @Autowired
    public void setJdbcClient( @Qualifier("clickHouseJdbcClient") JdbcClient jdbcClient ) {
        this.jdbcClient = jdbcClient;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Autowired
    public void setJdbcTemplate( @Qualifier("clickHouseTemplate") JdbcTemplate jdbcTemplate ) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
