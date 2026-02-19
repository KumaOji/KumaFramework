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

package com.kuma.boot.data.mybatis.interceptor.sqlanalysis.score;

/**
 * @Author huhaitao21
 * @Description sql 评分结果
 * @since 18:33 2022/11/2
 **/
public class SqlScoreResultDetail {

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
     * 是否严格规则，是的-直接触发警告，否-依赖综合评分进行警告（暂不使用）
     */
    private Boolean strict;

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
