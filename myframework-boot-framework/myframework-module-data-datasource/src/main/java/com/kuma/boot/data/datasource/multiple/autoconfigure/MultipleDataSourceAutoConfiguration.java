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

package com.kuma.boot.data.datasource.multiple.autoconfigure;

import com.kuma.boot.data.datasource.autoconfigure.properties.DataSourceProperties;
import com.kuma.boot.data.datasource.multiple.autoconfigure.properties.MultipleDataSourceCommonProperties;
import com.kuma.boot.data.datasource.multiple.autoconfigure.properties.MultipleDataSourceProperties;
import com.kuma.boot.data.datasource.multiple.ck.ClickHouseDataSourceConfiguration;
import com.kuma.boot.data.datasource.multiple.doris.DorisDataSourceConfiguration;
import com.kuma.boot.data.datasource.multiple.hive.HiveDataSourceConfiguration;
import com.kuma.boot.data.datasource.multiple.tidb.TidbDataSourceConfiguration;
import com.kuma.boot.data.datasource.multiple.trino.TrinoDataSourceConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * ExtDataSourceAutoConfiguration
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@AutoConfiguration
@EnableConfigurationProperties({MultipleDataSourceCommonProperties.class, MultipleDataSourceProperties.class})
@Import({
        ClickHouseDataSourceConfiguration.class,
        DorisDataSourceConfiguration.class,
        HiveDataSourceConfiguration.class,
        TidbDataSourceConfiguration.class,
        TrinoDataSourceConfiguration.class
})
@ConditionalOnProperties(value = {
        @ConditionalOnProperty(
                prefix = MultipleDataSourceProperties.PREFIX,
                name = "enabled",
                havingValue = "true"),
})
public class MultipleDataSourceAutoConfiguration {

}
