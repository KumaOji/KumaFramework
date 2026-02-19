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

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.analysis.SqlAnalysisResult;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.analysis.SqlAnalysisResultList;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.config.SqlAnalysisConfig;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.rule.SqlScoreRule;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.util.GsonUtil;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author huhaitao21
 * @Description 评分服务默认实现
 * @since 20:43 2022/11/1
 **/
@Deprecated
public class SqlScoreServiceDefault implements SqlScoreService {

    private static Logger logger = LoggerFactory.getLogger(SqlScoreServiceDefault.class);

    private static final Integer WARN_SCORE = 80;

    @Override
    public SqlScoreResult score(SqlAnalysisResultList sqlAnalysisResultList) {
        if (sqlAnalysisResultList == null
                || CollectionUtils.isEmpty(sqlAnalysisResultList.getResultList())) {
            return null;
        }
        // 默认100分，扣分制
        Integer score = 100;
        SqlScoreResult scoreResult = new SqlScoreResult();

        List<SqlScoreResultDetail> analysisResults = new ArrayList<>();
        // 遍历分析结果,匹配评分规则
        for (SqlAnalysisResult result : sqlAnalysisResultList.getResultList()) {
            List<SqlScoreResultDetail> detail = matchRule(result);
            if (CollectionUtils.isNotEmpty(detail)) {
                analysisResults.addAll(detail);
            }
        }

        // 综合评分计算
        for (SqlScoreResultDetail detail : analysisResults) {
            score = score - detail.getScoreDeduction();
            if (score < 0) {
                // 防止出现负分
                score = 0;
            }

            if (score < WARN_SCORE) {
                scoreResult.setNeedWarn(true);
            } else {
                scoreResult.setNeedWarn(false);
            }
        }
        scoreResult.setScore(score);
        scoreResult.setAnalysisResults(analysisResults);

        logger.info("sql analysis result = " + GsonUtil.bean2Json(scoreResult));
        return scoreResult;
    }

    /**
     * 规则匹配 返回 计算明细
     *
     * @param result
     * @return
     */
    private List<SqlScoreResultDetail> matchRule(SqlAnalysisResult result) {
        List<SqlScoreResultDetail> detailList = new ArrayList<>();

        if (CollectionUtils.isEmpty(SqlAnalysisConfig.getRuleList())) {
            return null;
        }
        for (SqlScoreRule sqlScoreRule : SqlAnalysisConfig.getRuleList()) {
            try {
                SqlScoreResultDetail detail = new SqlScoreResultDetail();
                // 根据属性，获取属性值
                String columnName = sqlScoreRule.getMatchColumn().getColumn();
                Object value = getValue(result, columnName);
                if (value == null) {
                    continue;
                }
                // 根据匹配规则对属性进行匹配
                boolean matchResult = matchColumn(sqlScoreRule, value);

                if (matchResult) {
                    detail.setScoreDeduction(sqlScoreRule.getScoreDeduction());
                    detail.setReason(sqlScoreRule.getReason());
                    detail.setSuggestion(sqlScoreRule.getSuggestion());
                    detail.setStrict(sqlScoreRule.getStrict());
                    detailList.add(detail);
                }

            } catch (Exception e) {
                LogUtils.error(e);
                logger.error("sql analysis matchRule error:", e);
            }
        }
        return detailList;
    }

    /**
     * 根据字段名称提取属性值
     *
     * @param result
     * @param columnName
     * @return
     */
    private Object getValue(SqlAnalysisResult result, String columnName) {
        try {
            String methodName =
                    "get" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
            Class sqlAnalysisResultClass = SqlAnalysisResult.class;
            Method getMethod = sqlAnalysisResultClass.getDeclaredMethod(methodName);
            Object value = getMethod.invoke(result);
            return value;
        } catch (Exception e) {
            logger.error("sql analysis get value error :", e);
        }
        return null;
    }

    /**
     * 匹配字段值
     *
     * @param sqlScoreRule
     * @param value        字段值
     * @return
     */
    private boolean matchColumn(SqlScoreRule sqlScoreRule, Object value) {
        boolean matchResult = false;
        switch (sqlScoreRule.getMatchType()) {
            case EQUAL:
                if (value.toString().equals(sqlScoreRule.getMatchValue())) {
                    matchResult = true;
                }
                break;
            case GREATER:
                if (Double.parseDouble(value.toString())
                        > Double.parseDouble(sqlScoreRule.getMatchValue())) {
                    matchResult = true;
                }
                break;
            case LESS:
                if (Double.parseDouble(value.toString())
                        < Double.parseDouble(sqlScoreRule.getMatchValue())) {
                    matchResult = true;
                }
                break;
            case CONTAIN:
                if (value.toString().contains(sqlScoreRule.getMatchValue())) {
                    matchResult = true;
                }
                break;
            default:
                ;
        }
        return matchResult;
    }
}
