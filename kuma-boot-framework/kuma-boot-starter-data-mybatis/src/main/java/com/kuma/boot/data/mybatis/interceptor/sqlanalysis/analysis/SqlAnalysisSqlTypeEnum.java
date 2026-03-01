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

package com.kuma.boot.data.mybatis.interceptor.sqlanalysis.analysis;

/**
 * @Author huhaitao21
 * @Description sql 分析组件支持分析的sql类型
 * @since 19:36 2022/11/3
 **/
public enum SqlAnalysisSqlTypeEnum {
    SELECT("SELECT", "查询"),
    UPDATE("UPDATE", "更新"),
    INSERT("INSERT", "插入"),
    DELETE("DELETE", "删除");

    SqlAnalysisSqlTypeEnum(String type, String description) {
        this.type = type;
        this.description = description;
    }

    /**
     * sql类型
     */
    private String type;

    /**
     * 描述
     */
    private String description;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
