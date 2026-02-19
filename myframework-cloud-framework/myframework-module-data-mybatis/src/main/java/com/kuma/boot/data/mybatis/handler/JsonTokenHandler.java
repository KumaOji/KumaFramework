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

import tools.jackson.databind.JsonNode;
import com.kuma.boot.common.utils.log.LogUtils;
import org.apache.ibatis.parsing.TokenHandler;
import tools.jackson.databind.json.JsonMapper;

/**
 * JsonTokenHandler
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class JsonTokenHandler implements TokenHandler {

    private final JsonMapper jsonMapper;
    private JsonNode jsonNode;

    private final com.kuma.boot.data.mybatis.handler.SpFieldHandler<JsonNode> spFieldHandler;

    /**
     * 构造函数，接收一个 JSON 字符串作为参数，并初始化 JsonMapper 和 JsonNode
     *
     * @param json json
     */
    public JsonTokenHandler( String json, com.kuma.boot.data.mybatis.handler.SpFieldHandler<JsonNode> spFieldHandler ) {
        this.spFieldHandler = spFieldHandler;
        jsonMapper = JsonMapper.builder().build();
        try {
            // 将 JSON 字符串解析为 JsonNode 对象
            jsonNode = jsonMapper.readTree(json);
        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

    @Override
    public String handleToken( String content ) {
        String value = "'-'";

        if (jsonNode != null) {
            String[] fieldPath = content.split("\\.");

            JsonNode currentNode = jsonNode;

            for (String fieldName : fieldPath) {
                if (currentNode.isObject()) {
                    currentNode = currentNode.get(fieldName);
                } else {
                    // 如果当前节点不是对象，则尝试解析为字符串形式的 JSON
                    try {
                        JsonNode jsonValue = jsonMapper.readTree(currentNode.asString());
                        if (jsonValue != null && jsonValue.isObject()) {
                            currentNode = jsonValue.get(fieldName);
                        } else {
                            break;
                        }
                    } catch (Exception e) {
                        break;
                    }
                }

                if (currentNode == null) {
                    break;
                }
            }
            // 将json中解析到的数据做特殊处理
            value = spFieldHandler.parseSpFieldValue(content, currentNode);
        }
        return "null".equals(value) ? "'-'" : value;
    }
}
