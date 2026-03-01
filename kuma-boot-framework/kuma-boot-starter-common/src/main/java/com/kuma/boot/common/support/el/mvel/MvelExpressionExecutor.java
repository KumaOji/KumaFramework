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

package com.kuma.boot.common.support.el.mvel;

// import org.mvel2.MVEL;

import com.kuma.boot.common.support.el.ExpressionExecutor;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MVEL表达式执行器
 *
 */
public final class MvelExpressionExecutor implements ExpressionExecutor {

    /**
     * 脚本编译后的类缓存
     */
    private static final Map<String, Serializable> compiledExpressionMap =
            new ConcurrentHashMap<>();

    /**
     * 编译脚本 如果已编译则不进行编译 用来进行预编译
     * @param scriptText 脚本
     */
    // public void compile(String scriptText) {
    // getOrCompile(scriptText);
    // }

    /**
     * 获取已编译的脚本或编译
     * @param scriptText 脚本
     * @return 编译后的对象
     */
    // private Serializable getOrCompile(String scriptText) {
    // Serializable compiledExpression = compiledExpressionMap.get(scriptText);
    // if (compiledExpression == null) {
    // compiledExpression = compiledExpressionMap.computeIfAbsent(scriptText,
    // MVEL::compileExpression);
    // }
    // return compiledExpression;
    // }

    /**
     * 执行脚本
     * @param expressionText 脚本
     * @param variables 变量
     * @return 执行后脚本的返回结果
     */
    @Override
    public Object execute(String expressionText, Map<String, Object> variables) {
        // return MVEL.executeExpression(getOrCompile(expressionText), variables);
        return null;
    }
}
