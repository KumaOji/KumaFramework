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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.service.impl;


import static com.kuma.boot.mq.kafka.kafka.constant.KafkaConstant.DATA_VERSION_LOG_TOPIC;

import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.model.DataVersionLogData;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datachanage.service.DataVersionLogService;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * DataVersionLogKafkaService
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class DataVersionLogKafkaService implements DataVersionLogService {

    public DataVersionLogKafkaService( KafkaTemplate<String, Object> kafkaTemplate ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void add( DataVersionLogData dataVersionLogData ) {
        String data = JacksonUtils.toJSONString(dataVersionLogData);
        kafkaTemplate.send(DATA_VERSION_LOG_TOPIC, data);
    }
}
