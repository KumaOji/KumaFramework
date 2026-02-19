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

package com.kuma.boot.data.mybatis.handler;

import org.apache.ibatis.parsing.TokenHandler;

/**
 * MessageBuildUtils
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class MessageBuildUtils {

    /**
     * 告警消息，根据json填充模板
     *
     * @param json json字符串
     * @param template 消息模板
     * @return 构造完成的消息
     */
    public static String buildMsgByJson( String json, String template ) {
        // 创建 TokenHandler 处理器
        TokenHandler handler = new JsonTokenHandler(json, null);

        // 创建 TemplateParser 解析器
        com.kuma.boot.data.mybatis.handler.TemplateParser parser = new com.kuma.boot.data.mybatis.handler.TemplateParser(handler);

        // 解析并替换占位符为实际值
        return parser.parseTemplate(template);
    }

    /**
     * 根据对象填充模板
     *
     * @param obj 对象
     * @param template 消息模板
     * @return 构造完成的消息
     */
    public static String buildMsgByObj( Object obj, String template ) {
        // 创建 TokenHandler 处理器
        TokenHandler handler = new com.kuma.boot.data.mybatis.handler.ObjectTokenHandler(obj);

        // 创建 TemplateParser 解析器
        com.kuma.boot.data.mybatis.handler.TemplateParser parser = new com.kuma.boot.data.mybatis.handler.TemplateParser(handler);

        // 解析并替换占位符为实际值
        return parser.parseTemplate(template);
    }
}
