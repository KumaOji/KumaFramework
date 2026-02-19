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

import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.analysis.SqlAnalysisResult;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.analysis.SqlAnalysisResultList;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.rule.RulesEngineExecutor;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.util.GsonUtil;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author huhaitao21
 * @Description 采用规则引擎计算评分实现方式
 * @since 20:43 2022/11/1
 **/
public class SqlScoreServiceRulesEngine implements SqlScoreService {

    private static Logger logger = LoggerFactory.getLogger(SqlScoreServiceRulesEngine.class);

    private static final Integer WARN_SCORE = 80;

    @Override
    public SqlScoreResult score(SqlAnalysisResultList sqlAnalysisResultList) {
        if (sqlAnalysisResultList == null
                || CollectionUtils.isEmpty(sqlAnalysisResultList.getResultList())) {
            return null;
        }
        // 默认100分，扣分制
        int score = 100;
        SqlScoreResult scoreResult = new SqlScoreResult();

        List<SqlScoreResultDetail> analysisResults = new ArrayList<>();
        // 遍历分析结果,匹配评分规则
        for (SqlAnalysisResult result : sqlAnalysisResultList.getResultList()) {
            List<SqlScoreResultDetail> detail = matchRuleEngine(result);
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

    private List<SqlScoreResultDetail> matchRuleEngine(SqlAnalysisResult result) {
        return RulesEngineExecutor.executeEngine(result);
    }
}
