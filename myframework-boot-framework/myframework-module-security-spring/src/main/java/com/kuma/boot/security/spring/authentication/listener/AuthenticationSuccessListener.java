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

package com.kuma.boot.security.spring.authentication.listener;

import com.kuma.boot.security.spring.authentication.compliance.service.ComplianceService;
import com.kuma.boot.security.spring.authentication.stamp.SignInFailureLimitedStampManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

/**
 * <p>登录成功事件监听 </p>
 */
public class AuthenticationSuccessListener
        implements ApplicationListener<AuthenticationSuccessEvent> {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationSuccessListener.class);

    private final SignInFailureLimitedStampManager stampManager;
    private final ComplianceService complianceService;
    private final com.kuma.boot.security.spring.authentication.listener.AuthenticationSuccessHandler authenticationSuccessHandler;

    public AuthenticationSuccessListener(
            SignInFailureLimitedStampManager stampManager,
            ComplianceService complianceService,
            com.kuma.boot.security.spring.authentication.listener.AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.stampManager = stampManager;
        this.complianceService = complianceService;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {

        log.debug(" Authentication Success Listener!");

        Authentication authentication = event.getAuthentication();

        authenticationSuccessHandler.handle(event);

        //		if (authentication instanceof OAuth2AccessTokenAuthenticationToken authenticationToken)
        // {
        //			Object details = authentication.getDetails();
        //
        //			String username = null;
        //			if (ObjectUtils.isNotEmpty(details) && details instanceof PrincipalDetails user) {
        //				username = user.getUserName();
        //			}
        //
        //			String clientId = authenticationToken.getRegisteredClient().getId();
        //
        //			RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //			if (ObjectUtils.isNotEmpty(requestAttributes)
        //				&& requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
        //				HttpServletRequest request = servletRequestAttributes.getRequest();
        //
        //				if (ObjectUtils.isNotEmpty(request) && StringUtils.isNotBlank(username)) {
        //					complianceService.save(username, clientId, "用户登录", request);
        //					String key = SecureUtil.md5(username);
        //					boolean hasKey = stampManager.containKey(key);
        //					if (hasKey) {
        //						stampManager.delete(key);
        //					}
        //				}
        //			}
        //			else {
        //				log.warn(" Can not get request and username, skip!");
        //			}
        //		}
    }
}
