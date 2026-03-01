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

package com.kuma.boot.data.mybatis.multiple.trino;

import com.kuma.boot.data.datasource.multiple.trino.TrinoDataSourceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * TrinoMybatisConfiguration
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@AutoConfiguration(after = TrinoDataSourceConfiguration.class)
@ConditionalOnProperty(name = "kuma.boot.data.datasource.multiple.trino.enabled", havingValue = "true")
public class TrinoMybatisConfiguration {

    private static Logger logger = LoggerFactory.getLogger(TrinoMybatisConfiguration.class);
}
