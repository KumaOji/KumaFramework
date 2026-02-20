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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.hutool.core.date.DateUtil;

/**
 * AlarmJsonSpHandler
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class AlarmJsonSpHandler implements com.kuma.boot.data.mybatis.handler.SpFieldHandler<JsonNode> {

    // 特殊字段
    List<String> spFieldList = Arrays.asList("startTime", "endTime", "state", "alarmSource");

    @Override
    public String parseSpFieldValue( String filedName, JsonNode node ) {
        if (spFieldList.contains(filedName)) {
            // 如果是特殊字段
            switch (filedName) {
                case "startTime":
                case "endTime": {
                    return DateUtil.format(new Date(node.asLong()), "yyyy.MM.dd HH:mm:ss");
                }
                case "state": {
                    // return AlertStatusEnum.getValueByCode(node.asInt());
                }
                case "alarmSource": {
                    // return AlertOriginEnum.getValueByCode(node.asInt());
                }

                default:
                    return node.asString();
            }
        } else {
            // 非特殊字段
            return node != null ? node.asString() : "null";
        }
    }
}
