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

package com.kuma.boot.web.aop.aop.util;

import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * SpEL的工具类
 *
 * @author sunbo
 */
public class SpringExpressionUtils {

    /**
     * 解析SpEL表达式
     *
     * @param spel   表达式
     * @param method 方法
     * @param args   参数
     * @param clazz  需要返回的类型
     * @return 执行表达式后的结果
     */
    public static <T> T parse(String spel, Method method, Object[] args, Class<T> clazz) {
        // 表达式的context
        EvaluationContext context = new StandardEvaluationContext();

        if (args != null) {
            String[] parameterNames =
                    new StandardReflectionParameterNameDiscoverer().getParameterNames(method);
            if (parameterNames != null) {
                for (int i = 0, len = args.length; i < len; i++) {
                    context.setVariable(parameterNames[i], args[i]);
                }
            }
        }

        // 解析表达式
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
        Expression expression = spelExpressionParser.parseExpression(spel);

        return expression.getValue(context, clazz);
    }

    /**
     * 解析SpEL的模板
     *
     * @param template 模板
     * @param object   参数对象
     * @return 执行后的字符串
     */
    public static String parseTemplate(String template, Object object) {
        TemplateParserContext templateParserContext = new TemplateParserContext();
        ExpressionParser parser = new SpelExpressionParser();
        // new PropertySourcesPropertyResolver
        Expression expression = parser.parseExpression(template, templateParserContext);
        return expression.getValue(object, String.class);
    }
}
