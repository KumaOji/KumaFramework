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

package com.kuma.boot.data.mybatis.interceptor.sqlanalysis.rule;

/**
 * @Author huhaitao21
 * @Description 评分规则
 * @since 15:13 2022/11/9
 **/
public class SqlScoreRule {

    /**
     * 检查字段
     */
    private MatchColumn matchColumn;

    /**
     * 匹配值
     */
    private String matchValue;

    /**
     * 匹配规则
     */
    private MatchType matchType;

    /**
     * 减分值
     */
    private Integer scoreDeduction;

    /**
     * 原因
     */
    private String reason;

    /**
     * 建议
     */
    private String suggestion;

    /**
     * 是否严格规则，是的-直接触发警告，否-依赖综合评分进行警告
     */
    private Boolean strict;

    public MatchColumn getMatchColumn() {
        return matchColumn;
    }

    public void setMatchColumn(MatchColumn matchColumn) {
        this.matchColumn = matchColumn;
    }

    public String getMatchValue() {
        return matchValue;
    }

    public void setMatchValue(String matchValue) {
        this.matchValue = matchValue;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public Integer getScoreDeduction() {
        return scoreDeduction;
    }

    public void setScoreDeduction(Integer scoreDeduction) {
        this.scoreDeduction = scoreDeduction;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public Boolean getStrict() {
        return strict;
    }

    public void setStrict(Boolean strict) {
        this.strict = strict;
    }
}
