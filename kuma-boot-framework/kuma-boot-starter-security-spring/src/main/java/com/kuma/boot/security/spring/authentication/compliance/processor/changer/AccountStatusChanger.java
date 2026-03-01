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

import com.kuma.boot.security.spring.event.ApplicationStrategyEvent;
import com.kuma.boot.security.spring.event.domain.KmcUserStatus;

/**
 * <p>用户状态变更服务 </p>
 *
 */
public interface AccountStatusChanger extends ApplicationStrategyEvent<KmcUserStatus> {

    /**
     * Request Mapping 收集汇总的服务名称
     *
     * @return 服务名称
     */
    String getDestinationServiceName();

    default void process(KmcUserStatus status) {
        postProcess(getDestinationServiceName(), status);
    }
}
