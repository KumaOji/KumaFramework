/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.apache.commons.lang3.ObjectUtils
 *  org.springframework.data.domain.AuditorAware
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.context.SecurityContext
 *  org.springframework.security.core.context.SecurityContextHolder
 *  org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication
 *  org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal
 *  org.springframework.stereotype.Component
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

@Component
public class SecurityAuditorAware
implements AuditorAware<String> {
    public Optional<String> getCurrentAuditor() {
        BearerTokenAuthentication bearerTokenAuthentication;
        Object object;
        Authentication authentication;
        SecurityContext context = SecurityContextHolder.getContext();
        if (ObjectUtils.isNotEmpty((Object)context) && ObjectUtils.isNotEmpty((Object)(authentication = context.getAuthentication())) && authentication.isAuthenticated() && authentication instanceof BearerTokenAuthentication && (object = (bearerTokenAuthentication = (BearerTokenAuthentication)authentication).getPrincipal()) instanceof OAuth2IntrospectionAuthenticatedPrincipal) {
            OAuth2IntrospectionAuthenticatedPrincipal principal = (OAuth2IntrospectionAuthenticatedPrincipal)object;
            String username = principal.getName();
            LogUtils.info((String)"Current auditor is : [{}]", (Object[])new Object[]{username});
            return Optional.of(username);
        }
        return Optional.empty();
    }
}

