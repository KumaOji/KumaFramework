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

import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.analysis.SqlAnalysisResult;
import com.kuma.boot.data.mybatis.interceptor.sqlanalysis.score.SqlScoreResultDetail;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.mvel.MVELRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 规则引擎执行器
 *
 * @author yangchao341
 */
public class RulesEngineExecutor {

    private static Logger logger = LoggerFactory.getLogger(RulesEngineExecutor.class);

    public static final RulesEngine rulesEngine = new DefaultRulesEngine();
    private static Rules rules;

    /**
     * 匹配规则-评分结果映射关系
     */
    private static ConcurrentHashMap<String, SqlScoreResultDetail> scoreMap;

    public static boolean refresh() {
        InputStream inputStream;
        Properties properties = new Properties();
        inputStream =
                RulesEngineExecutor.class
                        .getClassLoader()
                        .getResourceAsStream("sql-analysis-rule-config.properties");
        try {
            properties.load(new InputStreamReader(inputStream));
            inputStream.close();
        } catch (IOException e) {
            logger.error("规则引擎配置文件加载失败", e);
        }
        Map<String, MVELRule> ruleMap = new HashMap<String, MVELRule>();
        ConcurrentHashMap<String, SqlScoreResultDetail> innerScoreMap =
                new ConcurrentHashMap<String, SqlScoreResultDetail>();
        properties.forEach(
                (key, value) -> {
                    String[] keyArr = StringUtils.split(key.toString(), '.').toArray(new String[0]);
                    if (keyArr.length >= 2) {
                        MVELRule rule =
                                ruleMap.getOrDefault(
                                        keyArr[0],
                                        new MVELRule()
                                                .name(keyArr[0])
                                                .then("ret.add(\"" + keyArr[0] + "\")"));
                        SqlScoreResultDetail score =
                                innerScoreMap.getOrDefault(keyArr[0], new SqlScoreResultDetail());
                        switch (RuleFieldEnum.valueOf(keyArr[1].toUpperCase())) {
                            case CONDITION:
                                rule.when(value.toString());
                                break;
                            case ACTION:
                                rule.then(value.toString());
                                break;
                            case NAME:
                                rule.name(value.toString());
                                break;
                            case DESCRIPTION:
                                rule.description(value.toString());
                                break;
                            case PRIORITY:
                                rule.priority(NumberUtils.toInt(value.toString()));
                                break;
                            case SCORE:
                                score.setScoreDeduction(NumberUtils.toInt(value.toString()));
                                break;
                            case REASON:
                                score.setReason(value.toString());
                                break;
                            case SUGGESTION:
                                score.setSuggestion(value.toString());
                                break;
                            case STRICT:
                                score.setStrict(Boolean.valueOf(value.toString()));
                                break;
                        }
                        ruleMap.put(keyArr[0], rule);
                        innerScoreMap.put(keyArr[0], score);
                    }
                });
        Rules newRules = new Rules();
        for (Map.Entry<String, MVELRule> ruleEntry : ruleMap.entrySet()) {
            newRules.register(ruleEntry.getValue());
        }
        rules = newRules;
        scoreMap = innerScoreMap;

        return true;
    }

    public static List<SqlScoreResultDetail> executeEngine(SqlAnalysisResult sqlAnalysisResult) {
        ArrayList<SqlScoreResultDetail> retList = new ArrayList<SqlScoreResultDetail>();
        ArrayList<String> ret = new ArrayList<String>();
        // 创建事实
        Facts facts = new Facts();
        facts.put("param", sqlAnalysisResult);
        facts.put("ret", ret);
        // 执行规则
        rulesEngine.fire(rules, facts);
        ret = facts.get("ret");
        for (String item : ret) {
            if (scoreMap.get(item) != null) {
                retList.add(scoreMap.get(item));
            }
        }
        return retList;
    }
}
