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

package com.kuma.cloud.ccsr.client.request;

import com.kuma.cloud.ccsr.api.event.EventType;
import com.kuma.cloud.ccsr.api.grpc.auto.MetadataType;
import com.kuma.cloud.ccsr.client.listener.ConfigData;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

/**
 * Payload
 *
 * @author kuma
 * @version 2026.02
 * @since 2025-12-19 09:30:45
 */
@Data
@Builder
public class Payload {

    private String namespace;

    private String group;

    private String tag;

    private String dataId;

    private ConfigData configData;

    private MetadataType type;

    private Map<String, String> ext;

    private Long gmtCreate;

    private Long gmtModified;

    private EventType eventType;
}
