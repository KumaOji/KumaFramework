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

package com.kuma.cloud.ccsr.example.listener;

import com.kuma.cloud.ccsr.api.event.EventType;
import com.kuma.cloud.ccsr.client.listener.AbstractConfigListener;
import com.kuma.cloud.ccsr.common.log.Log;
import com.kuma.cloud.ccsr.example.dto.User;
import com.kuma.cloud.ccsr.spi.Join;

/**
 * UserConfigListener
 *
 * @author kuma
 * @version 2026.02
 * @since 2025-12-19 09:30:45
 */
@Join
public class UserConfigListener extends AbstractConfigListener<User> {

    @Override
    public void receive( String dataStr, User data, EventType eventType ) {
        // TODO: Implement the logic to handle the received data
        Log.print("客户端收到配置变更推送: eventType=%s, dataStr=%s", eventType, dataStr);
    }
}
