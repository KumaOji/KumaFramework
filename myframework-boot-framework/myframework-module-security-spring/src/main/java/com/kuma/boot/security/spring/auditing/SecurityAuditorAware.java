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

package com.kuma.boot.security.spring.auditing;

import com.kuma.boot.common.utils.log.LogUtils;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.stereotype.Component;

/**
 * <p>基于 Security 的数据库审计用户信息获取
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 09:56:45
 */
@Component
public class SecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (ObjectUtils.isNotEmpty(context)) {
            Authentication authentication = context.getAuthentication();
            if (ObjectUtils.isNotEmpty(authentication)) {
                if (authentication.isAuthenticated()) {
                    if (authentication
                            instanceof BearerTokenAuthentication bearerTokenAuthentication) {
                        Object object = bearerTokenAuthentication.getPrincipal();
                        if (object instanceof OAuth2IntrospectionAuthenticatedPrincipal principal) {
                            String username = principal.getName();
                            LogUtils.info("Current auditor is : [{}]", username);
                            return Optional.of(username);
                        }
                    }
                }
            }
        }

        return Optional.empty();
    }
}
