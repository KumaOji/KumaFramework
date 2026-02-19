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

package com.kuma.boot.data.mybatis.multiple;

import com.kuma.boot.data.mybatis.multiple.ck.ClickHouseMybatisConfiguration;
import com.kuma.boot.data.mybatis.multiple.doris.DorisMybatisConfiguration;
import com.kuma.boot.data.mybatis.multiple.hive.HiveMybatisConfiguration;
import com.kuma.boot.data.mybatis.multiple.tidb.TidbMybatisConfiguration;
import com.kuma.boot.data.mybatis.multiple.trino.TrinoMybatisConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * ExtMysqlAutoConfiguration
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@AutoConfiguration
@Import({
        ClickHouseMybatisConfiguration.class,
        DorisMybatisConfiguration.class,
        HiveMybatisConfiguration.class,
        TidbMybatisConfiguration.class,
        TrinoMybatisConfiguration.class
})
public class MultipleMybatisAutoConfiguration {

}
