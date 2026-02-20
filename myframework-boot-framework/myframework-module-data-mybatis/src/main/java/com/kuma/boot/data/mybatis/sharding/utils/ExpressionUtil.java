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

package com.kuma.boot.data.mybatis.sharding.utils;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.Expression;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用轻量级表达式引擎来计算， 经测试微秒基别性能损耗
 * 基于 aviator 表达式引擎，
 *
 * @author winjeg
 */
public class ExpressionUtil {
    private static final AviatorEvaluatorInstance EVALUATOR = AviatorEvaluator.getInstance();

    /**
     * 评估一个表达式，计算出最终结果
     */
    public static String eval(String expStr, String varName, long varValue) {
        Map<String, Object> map = new HashMap<>();
        map.put(varName, varValue);
        return eval(expStr, map);
    }

    public static String eval(String expStr, Map<String, Object> expMap) {
        Expression e = EVALUATOR.compile(expStr, true);
        return (String) e.execute(expMap);
    }
}
