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

package com.kuma.boot.common.support.el;

import com.alibaba.fastjson2.util.TypeUtils;
import java.util.Map;

/**
 * 表达式执行器
 */
public interface ExpressionExecutor {

    /**
     * 执行表达式
     * @param expressionText 表达式文本
     * @param variables 变量
     * @return 执行后脚本的返回结果
     */
    Object execute(String expressionText, Map<String, Object> variables);

    /**
     * 执行表达式
     * @param expressionText 表达式文本
     * @param variables 变量
     * @param resultType 结果类型
     * @param <T> 结果类型
     * @return 执行后脚本的返回结果
     */
    default <T> T execute(
            String expressionText, Map<String, Object> variables, Class<T> resultType) {
        return TypeUtils.cast(execute(expressionText, variables), resultType, null);
    }
}
