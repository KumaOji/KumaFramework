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

package com.kuma.boot.data.datasource.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidStatManagerFacade;
import jakarta.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * DruidStatController
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@RestController
@RequestMapping(value = "/druid")
public class DruidStatController {

    /**
     * Spring Boot 默认已经配置好了数据源，程序员可以直接 DI 注入然后使用即可
     */
    @Resource
    private DataSource dataSource;

    /**
     * 获取 druid 数据监控信息，其中统计了所有数据源的所有详细信息。
     */
    @GetMapping("/stat")
    public List<Map<String, Object>> druidStat() {
        // 获取数据源的监控数据
        return DruidStatManagerFacade.getInstance().getDataSourceStatDataList();
    }

    /**
     * 使用 DruidDataSource API 获取具体数据源的指定的监控信息。
     */
    @GetMapping("/druidDataSource")
    public Map<String, Object> druidDataSource() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("class", dataSource.getClass());
        if (dataSource instanceof DruidDataSource) {
            DruidDataSource druidDataSource = (DruidDataSource) dataSource;
            dataMap.put("version", druidDataSource.getVersion());
            dataMap.put("name", druidDataSource.getName());
            dataMap.put("initialSize", druidDataSource.getInitialSize());
            dataMap.put("maxActive", druidDataSource.getMaxActive());
            dataMap.put("minIdle", druidDataSource.getMinIdle());
            dataMap.put("activeCount", druidDataSource.getActiveCount());
            dataMap.put("activePeak", druidDataSource.getActivePeak());
            dataMap.put("activePeakTime", druidDataSource.getActivePeakTime());
            dataMap.put("poolingCount", druidDataSource.getPoolingCount());
            dataMap.put("poolingPeak", druidDataSource.getPoolingPeak());
            dataMap.put("poolingPeakTime", druidDataSource.getPoolingPeakTime());
        }
        return dataMap;
    }
}
