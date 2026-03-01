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

package com.kuma.boot.security.spring.authorization.listener;

import com.kuma.boot.common.utils.log.LogUtils;
import org.springframework.context.event.EventListener;
import org.springframework.security.authorization.event.AuthorizationGrantedEvent;

/**
 * 授权成功的 ApplicationEvent。
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-12 11:09:09
 */
public class AuthorizationGrantedEventListener {

    @EventListener(AuthorizationGrantedEvent.class)
    public void authorizationDeniedEvent(AuthorizationGrantedEvent<?> event) {
        LogUtils.info("认证成功 authorizationDeniedEvent {}", event.getObject());
    }
}
