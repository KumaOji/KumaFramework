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

package com.kuma.cloud.ccsr.client.listener;

import com.kuma.cloud.ccsr.api.event.EventType;
import com.kuma.cloud.ccsr.client.dto.ServerAddress;
import com.kuma.cloud.ccsr.common.log.Log;

/**
 * ServerAddressConfigListener
 *
 * @author kuma
 * @version 2026.02
 * @since 2025-12-19 09:30:45
 */
public class ServerAddressConfigListener extends AbstractConfigListener<ServerAddress> {

    @Override
    public void receive( String dataStr, ServerAddress data, EventType eventType ) {
        // TODO: Implement the logic to handle the received ServerAddress configuration
        Log.print(
                "[INNER]ServerAddressConfigListener->客户端收到配置变更: eventType=%s, data=%s",
                eventType, dataStr);
    }
}
