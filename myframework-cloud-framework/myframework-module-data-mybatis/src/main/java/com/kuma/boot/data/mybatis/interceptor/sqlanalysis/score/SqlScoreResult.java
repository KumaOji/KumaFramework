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

package com.kuma.boot.data.mybatis.interceptor.sqlanalysis.score;

import java.util.List;

/**
 * @Author huhaitao21
 * @Description sql 评分结果
 * @since 18:33 2022/11/2
 **/
public class SqlScoreResult {

    /**
     * sql id
     */
    private String sqlId;

    /**
     * 执行的原始sql
     */
    private String sourceSql;

    /**
     * 是否需要警告
     */
    private Boolean needWarn;

    /**
     * 综合评分
     */
    private Integer score;

    /**
     * 分析结果明细
     */
    List<com.kuma.boot.data.mybatis.interceptor.sqlanalysis.score.SqlScoreResultDetail> analysisResults;

    public String getSqlId() {
        return sqlId;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }

    public String getSourceSql() {
        return sourceSql;
    }

    public void setSourceSql(String sourceSql) {
        this.sourceSql = sourceSql;
    }

    public Boolean getNeedWarn() {
        return needWarn;
    }

    public void setNeedWarn(Boolean needWarn) {
        this.needWarn = needWarn;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public List<com.kuma.boot.data.mybatis.interceptor.sqlanalysis.score.SqlScoreResultDetail> getAnalysisResults() {
        return analysisResults;
    }

    public void setAnalysisResults(List<com.kuma.boot.data.mybatis.interceptor.sqlanalysis.score.SqlScoreResultDetail> analysisResults) {
        this.analysisResults = analysisResults;
    }
}
