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

package com.kuma.boot.security.spring.authentication.compliance.processor.changer;

import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.security.spring.event.LocalChangeUserStatusEvent;
import com.kuma.boot.security.spring.event.RemoteChangeUserStatusEvent;
import com.kuma.boot.security.spring.event.domain.KmcUserStatus;

/**
 * <p>用户状态变更处理器 </p>
 *
 */
public class KmcAccountStatusChanger implements com.kuma.boot.security.spring.authentication.compliance.processor.changer.AccountStatusChanger {
    @Override
    public String getDestinationServiceName() {
        return "kuma-cloud-sys";
    }

    @Override
    public void postLocalProcess(KmcUserStatus data) {
        ContextUtils.getApplicationContext().publishEvent(new LocalChangeUserStatusEvent(data));
    }

    @Override
    public void postRemoteProcess(String data, String originService, String destinationService) {
        ContextUtils.getApplicationContext().publishEvent(new RemoteChangeUserStatusEvent(data));
    }
}
