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

package com.kuma.boot.data.mybatis.interceptor.sqlanalysis.replace;

import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.util.DuccMonitorUtil;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.util.GsonUtil;
import java.util.HashMap;
import java.util.Objects;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author huhaitao21
 * @Description sql替换配置
 * @since 18:50 2023/5/25
 **/
public class SqlReplaceConfig {

    static Logger log = LoggerFactory.getLogger(SqlReplaceConfig.class);

    /**
     * 配置明细
     */
    private static HashMap<String, String> sqlReplaceMap = new HashMap<>();

    /**
     * 初始化配置
     */
    public static void initConfig() {
        try {
            String configStr = DuccMonitorUtil.getDuccConfig();
            if (StringUtils.isNotBlank(configStr)) {
                sqlReplaceMap = GsonUtil.json2Bean(configStr, HashMap.class);
            }
        } catch (Exception e) {
            log.error("sql analysis replace config init error :", e);
        }
    }

    /**
     * 根据sqlid 获取替换sql
     * @param sqlId
     * @return
     */
    public static String getReplaceSqlBySqlId(String sqlId) {
        if (StringUtils.isNotBlank(sqlId) && Objects.nonNull(sqlReplaceMap)) {
            return sqlReplaceMap.get(sqlId);
        }
        return null;
    }

    /**
     * 获取sql替换映射对象
     * @return
     */
    public static HashMap<String, String> getSqlReplaceMap() {
        return sqlReplaceMap;
    }
}
