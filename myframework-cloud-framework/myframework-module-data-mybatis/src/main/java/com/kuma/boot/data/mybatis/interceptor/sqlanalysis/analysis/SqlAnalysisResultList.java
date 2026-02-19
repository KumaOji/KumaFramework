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

import java.util.List;

/**
 * @Author huhaitao21
 * @Description sql 分析结果 集合
 * @since 20:41 2022/11/1
 **/
public class SqlAnalysisResultList {

    /**
     * 分析结果集合
     */
    private List<SqlAnalysisResult> resultList;

    public List<SqlAnalysisResult> getResultList() {
        return resultList;
    }

    public void setResultList(List<SqlAnalysisResult> resultList) {
        this.resultList = resultList;
    }
}
