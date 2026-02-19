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

package com.kuma.boot.data.mybatis.interceptor.sqlanalysis.export;

import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.score.SqlScoreResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author huhaitao21
 * @Description 默认分析结果输出方式
 * @since 17:24 2022/11/7
 **/
public class SqlScoreResultOutServiceDefault implements SqlScoreResultOutService {

    private static Logger logger = LoggerFactory.getLogger(SqlScoreResultOutServiceDefault.class);

    @Override
    public void outResult(SqlScoreResult sqlScoreResult) {
        if (sqlScoreResult == null) {
            return;
        }
        if (sqlScoreResult.getNeedWarn() != null && sqlScoreResult.getNeedWarn()) {
            logger.error("sql analysis result score:{}", sqlScoreResult.getScore());
            if (sqlScoreResult.getAnalysisResults() != null) {
                sqlScoreResult
                        .getAnalysisResults()
                        .forEach(
                                result -> {
                                    logger.error(
                                            "sql analysis result detail-reason:{},suggestion:{}",
                                            result.getReason(),
                                            result.getSuggestion());
                                });
            }
        }
    }
}
