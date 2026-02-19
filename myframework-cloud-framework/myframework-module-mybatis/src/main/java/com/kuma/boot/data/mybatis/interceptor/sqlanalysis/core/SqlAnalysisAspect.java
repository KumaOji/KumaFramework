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

package com.kuma.boot.data.mybatis.interceptor.sqlanalysis.core;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.analysis.SqlAnalysis;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.analysis.SqlAnalysisResultList;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.config.JmqConfig;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.config.SqlAnalysisConfig;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.export.OutModelEnum;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.export.SqlScoreResultOutMq;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.export.SqlScoreResultOutService;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.export.SqlScoreResultOutServiceDefault;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.extract.SqlExtract;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.extract.SqlExtractResult;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.replace.SqlReplace;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.replace.SqlReplaceConfig;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.rule.SqlScoreRuleLoader;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.rule.SqlScoreRuleLoaderRulesEngine;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.score.SqlScoreResult;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.score.SqlScoreService;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.score.SqlScoreServiceRulesEngine;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.util.GsonUtil;
import java.sql.Connection;
import java.util.Properties;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author huhaitao21
 * @Description sql分析切面类
 * @since 22:47 2022/10/25
 **/
@Intercepts({
        @Signature(
                type = StatementHandler.class,
                method = "prepare",
                args = {Connection.class, Integer.class}),
        @Signature(
                type = Executor.class,
                method = "update",
                args = {MappedStatement.class, Object.class}),
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class SqlAnalysisAspect implements Interceptor {

    Logger logger = LoggerFactory.getLogger(SqlAnalysisAspect.class);

    /**
     * 评分规则服务
     */
    private static SqlScoreService sqlScoreService = new SqlScoreServiceRulesEngine();

    /**
     * 评分结果输出服务
     */
    private static SqlScoreResultOutService sqlScoreResultOut =
            new SqlScoreResultOutServiceDefault();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            Object firstArg = invocation.getArgs()[0];

            if (SqlAnalysisConfig.getSqlReplaceModelSwitch() != null
                    && SqlAnalysisConfig.getSqlReplaceModelSwitch()
                    && firstArg instanceof MappedStatement mappedStatement) {
                // sql替换模块
                String replaceSql = SqlReplaceConfig.getReplaceSqlBySqlId(mappedStatement.getId());
                if (StringUtils.isNotBlank(replaceSql)) {
                    SqlReplace.replace(invocation, replaceSql);
                }
            } else if (SqlAnalysisConfig.getAnalysisSwitch() && firstArg instanceof Connection) {
                // sql 分析模块
                // 获取入参statement
                StatementHandler statementHandler = (StatementHandler) invocation.getTarget();

                // 提取待执行的完整sql语句
                SqlExtractResult sqlExtractResult = SqlExtract.extract(statementHandler);
                if (sqlExtractResult != null) {
                    // 对sql进行分析
                    Connection connection = (Connection) invocation.getArgs()[0];
                    SqlAnalysisResultList resultList =
                            SqlAnalysis.analysis(sqlExtractResult, connection);

                    // 对分析结果进行评估
                    SqlScoreResult sqlScoreResult = sqlScoreService.score(resultList);
                    if (sqlScoreResult != null) {
                        sqlScoreResult.setSqlId(sqlExtractResult.getSqlId());
                        sqlScoreResult.setSourceSql(sqlExtractResult.getSourceSql());

                        // 输出评分结果
                        sqlScoreResultOut.outResult(sqlScoreResult);
                    } else {
                        logger.error(
                                "sql analysis score error {},{}",
                                GsonUtil.bean2Json(resultList),
                                GsonUtil.bean2Json(sqlExtractResult));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("sql analysis error ", e);
        }
        // 执行完上面的任务后，不改变原有的sql执行过程
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 初始化配置
        SqlAnalysisConfig.init(properties);

        // 初始化评分规则
        SqlScoreRuleLoader sqlScoreRuleLoader = new SqlScoreRuleLoaderRulesEngine();
        if (StringUtils.isNotBlank(SqlAnalysisConfig.getScoreRuleLoadClass())) {
            try {
                sqlScoreRuleLoader =
                        (SqlScoreRuleLoader)
                                Class.forName(SqlAnalysisConfig.getScoreRuleLoadClass())
                                        .newInstance();
            } catch (Exception e) {
                logger.error("sql analysis init score mode error", e);
            }
        }
        boolean loadScoreRuleRes = sqlScoreRuleLoader.loadScoreRule();
        if (!loadScoreRuleRes) {
            logger.error("sql analysis loadScoreRule exception");
        }
        // 初始化输出服务
        // mq方式输出
        if (StringUtils.isNotBlank(SqlAnalysisConfig.getOutputModel())
                && SqlAnalysisConfig.getOutputModel()
                .toUpperCase()
                .equals(OutModelEnum.MQ.getModelType())) {
            try {
                boolean result = JmqConfig.initMqProducer();
                if (result) {
                    SqlScoreResultOutService outServiceClass = new SqlScoreResultOutMq();
                    sqlScoreResultOut = outServiceClass;
                }
            } catch (Exception e) {
                LogUtils.error(e);
                logger.error("sql analysis init mq out mode error", e);
            }
        }

        // 自定义方式输出
        if (StringUtils.isNotBlank(SqlAnalysisConfig.getOutputClass())) {
            try {
                SqlScoreResultOutService outServiceClass =
                        (SqlScoreResultOutService)
                                Class.forName(SqlAnalysisConfig.getOutputClass()).newInstance();
                sqlScoreResultOut = outServiceClass;
            } catch (Exception e) {
                LogUtils.error(e);
                logger.error("sql analysis init out mode error", e);
            }
        }
    }
}
