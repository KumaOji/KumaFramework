/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.core.log.LogMessage
 *  org.springframework.security.core.GrantedAuthority
 *  org.springframework.security.oauth2.jwt.Jwt
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.security.spring.oauth2.authentication;

import com.kuma.boot.security.spring.core.authority.TtcGrantedAuthority;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class SecurityJwtGrantedAuthoritiesConverter
implements Converter<Jwt, Collection<GrantedAuthority>> {
    private final Log logger = LogFactory.getLog(this.getClass());
    private static final String DEFAULT_AUTHORITY_PREFIX = "SCOPE_";
    private static final Collection<String> WELL_KNOWN_AUTHORITIES_CLAIM_NAMES = Arrays.asList("scope", "scp");
    private String authorityPrefix = "SCOPE_";
    private String authoritiesClaimName;

    public Collection<GrantedAuthority> convert(Jwt jwt) {
        ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        for (String authority : this.getAuthorities(jwt)) {
            grantedAuthorities.add(new TtcGrantedAuthority(authority));
        }
        return grantedAuthorities;
    }

    public void setAuthorityPrefix(String authorityPrefix) {
        Assert.notNull((Object)authorityPrefix, (String)"authorityPrefix cannot be null");
        this.authorityPrefix = authorityPrefix;
    }

    public void setAuthoritiesClaimName(String authoritiesClaimName) {
        Assert.hasText((String)authoritiesClaimName, (String)"authoritiesClaimName cannot be empty");
        this.authoritiesClaimName = authoritiesClaimName;
    }

    private String getAuthoritiesClaimName(Jwt jwt) {
        if (this.authoritiesClaimName != null) {
            return this.authoritiesClaimName;
        }
        for (String claimName : WELL_KNOWN_AUTHORITIES_CLAIM_NAMES) {
            if (!jwt.hasClaim(claimName)) continue;
            return claimName;
        }
        return null;
    }

    private Collection<String> getAuthorities(Jwt jwt) {
        Object authorities;
        String claimName = this.getAuthoritiesClaimName(jwt);
        if (claimName == null) {
            this.logger.trace((Object)"Returning no authorities since could not find any claims that might contain scopes");
            return Collections.emptyList();
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)LogMessage.format((String)"Looking for scopes in claim %s", (Object)claimName));
        }
        if ((authorities = jwt.getClaim(claimName)) instanceof String) {
            if (StringUtils.hasText((String)((String)authorities))) {
                return Arrays.asList(((String)authorities).split(" "));
            }
            return Collections.emptyList();
        }
        if (authorities instanceof Collection) {
            return this.castAuthoritiesToCollection(authorities);
        }
        return Collections.emptyList();
    }

    private Collection<String> castAuthoritiesToCollection(Object authorities) {
        return (Collection)authorities;
    }
}

