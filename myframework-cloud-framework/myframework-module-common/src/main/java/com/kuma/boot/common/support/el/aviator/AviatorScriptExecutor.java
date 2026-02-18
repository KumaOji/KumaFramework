/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.support.el.aviator;

// import com.googlecode.aviator.AviatorEvaluator;

import com.kuma.boot.common.support.el.ExpressionExecutor;
import java.util.Map;

/**
 * Aviator脚本执行器
 *
 * @author sunbo
 */
public final class AviatorScriptExecutor implements ExpressionExecutor {

    /**
     * 校验脚本 如果校验失败会抛出异常
     * @param scriptText 脚本
     */
    // public void validate(String scriptText) {
    // AviatorEvaluator.validate(scriptText);
    // }

    /**
     * 编译脚本
     * @param scriptText 脚本
     */
    // public void compile(String scriptText) {
    // AviatorEvaluator.compile(scriptText, true);
    // }

    /**
     * 执行脚本
     * @param expressionText 脚本
     * @param variables 变量
     * @return 执行后脚本的返回结果
     */
    @Override
    public Object execute(String expressionText, Map<String, Object> variables) {
        // return AviatorEvaluator.execute(expressionText, variables, true);
        return null;
    }
}
