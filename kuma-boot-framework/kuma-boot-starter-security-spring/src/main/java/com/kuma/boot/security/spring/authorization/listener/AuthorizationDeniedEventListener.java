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
import jakarta.servlet.http.HttpServletRequest;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.event.EventListener;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;

/**
 * 授权拒绝事件侦听器
 *
 * <p>区分两类拒绝来源：
 * <ul>
 *   <li><b>HTTP 过滤链</b>：未登录用户访问受保护路径，属预期行为，记 DEBUG</li>
 *   <li><b>方法级安全</b>：{@code @Authorize}/{@code @PreAuthorize} 校验失败，记 WARN</li>
 * </ul>
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-12 11:09:07
 */
public class AuthorizationDeniedEventListener {

    @EventListener(AuthorizationDeniedEvent.class)
    public void authorizationDeniedEvent(AuthorizationDeniedEvent<?> event) {
        Object source = event.getObject();
        if (source instanceof HttpServletRequest request) {
            // 未认证用户访问受保护 URL，属正常现象，降为 DEBUG
            LogUtils.debug("HTTP 访问被拒绝 [{} {}]", request.getMethod(), request.getRequestURI());
        } else if (source instanceof MethodInvocation invocation) {
            // @Authorize/@PreAuthorize 方法级校验失败，记录方法信息
            LogUtils.warn("方法级授权拒绝 [{}.{}]",
                    invocation.getMethod().getDeclaringClass().getSimpleName(),
                    invocation.getMethod().getName());
        } else {
            LogUtils.warn("授权拒绝 authorizationDeniedEvent {}", source);
        }
    }
}
