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

// import org.mvel2.templates.Compiled_template;
// import_org_mvel_2_templates_template_compiler;
// import_org_mvel_2_templates_template_runtime;

import com.kuma.boot.common.support.el.ExpressionExecutor;
import java.util.Map;

/**
 * MVEL模板执行器
 *
 * @author sunbo
 */
public final class MvelTemplateExecutor implements ExpressionExecutor {

    /**
     * 脚本编译后的类缓存
     */
    // private static final Map<String, CompiledTemplate> compiledTemplateMap = new
    // ConcurrentHashMap<>();

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
    // private CompiledTemplate getOrCompile(String scriptText) {
    // CompiledTemplate compiledTemplate = compiledTemplateMap.get(scriptText);
    // if (compiledTemplate == null) {
    // compiledTemplate = compiledTemplateMap.computeIfAbsent(scriptText,
    // TemplateCompiler::compileTemplate);
    // }
    // return compiledTemplate;
    // }

    /**
     * 执行脚本
     * @param expressionText 脚本
     * @param variables 变量
     * @return 执行后脚本的返回结果
     */
    @Override
    public Object execute(String expressionText, Map<String, Object> variables) {
        // return TemplateRuntime.execute(getOrCompile(expressionText), variables);
        return null;
    }
}
