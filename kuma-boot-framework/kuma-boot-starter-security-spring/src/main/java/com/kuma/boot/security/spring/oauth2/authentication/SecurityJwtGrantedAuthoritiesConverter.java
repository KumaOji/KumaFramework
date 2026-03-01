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

package com.kuma.boot.security.spring.oauth2.authentication;

import com.kuma.boot.security.spring.core.authority.KmcGrantedAuthority;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * <p>Jwt 权限数据转换器
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 09:58:03
 */
public class SecurityJwtGrantedAuthoritiesConverter
        implements Converter<Jwt, Collection<GrantedAuthority>> {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(SecurityJwtGrantedAuthoritiesConverter.class);
    /**
     * 默认权威前缀
     */
    private static final String DEFAULT_AUTHORITY_PREFIX = "SCOPE_";

    /**
     * 众所周知当局声称名字
     */
    private static final Collection<String> WELL_KNOWN_AUTHORITIES_CLAIM_NAMES =
            Arrays.asList("scope", "scp");

    /**
     * 权威前缀
     */
    private String authorityPrefix = DEFAULT_AUTHORITY_PREFIX;

    /**
     * 当局声称名字
     */
    private String authoritiesClaimName;

    /**
     * Extract {@link GrantedAuthority}s from the given {@link Jwt}.
     *
     * @param jwt The
     * @return {@link Collection }<{@link GrantedAuthority }>
     * @since 2023-07-04 09:58:03
     */
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (String authority : getAuthorities(jwt)) {
            grantedAuthorities.add(new KmcGrantedAuthority(authority));
        }
        return grantedAuthorities;
    }

    /**
     * Sets the prefix to use for {@link GrantedAuthority authorities} mapped by this converter.
     * Defaults to
     *
     * @param authorityPrefix The authority prefix
     * @since 2023-07-04 09:58:03
     */
    public void setAuthorityPrefix(String authorityPrefix) {
        Assert.notNull(authorityPrefix, "authorityPrefix cannot be null");
        this.authorityPrefix = authorityPrefix;
    }

    /**
     * Sets the name of token claim to use for mapping {@link GrantedAuthority authorities} by this
     * converter. Defaults to
     *
     * @param authoritiesClaimName The token claim name to map authorities
     * @since 2023-07-04 09:58:03
     */
    public void setAuthoritiesClaimName(String authoritiesClaimName) {
        Assert.hasText(authoritiesClaimName, "authoritiesClaimName cannot be empty");
        this.authoritiesClaimName = authoritiesClaimName;
    }

    /**
     * 当局声称名字
     *
     * @param jwt jwt
     * @return {@link String }
     * @since 2023-07-04 09:58:03
     */
    private String getAuthoritiesClaimName(Jwt jwt) {
        if (this.authoritiesClaimName != null) {
            return this.authoritiesClaimName;
        }
        for (String claimName : WELL_KNOWN_AUTHORITIES_CLAIM_NAMES) {
            if (jwt.hasClaim(claimName)) {
                return claimName;
            }
        }
        return null;
    }

    /**
     * 得到当局
     *
     * @param jwt jwt
     * @return {@link Collection }<{@link String }>
     * @since 2023-07-04 09:58:03
     */
    private Collection<String> getAuthorities(Jwt jwt) {
        String claimName = getAuthoritiesClaimName(jwt);
        if (claimName == null) {
            logger.trace(
                    "Returning no authorities since could not find any claims that might contain scopes");
            return Collections.emptyList();
        }
        if (logger.isTraceEnabled()) {
            logger.trace(String.valueOf(LogMessage.format("Looking for scopes in claim %s", claimName)));
        }
        Object authorities = jwt.getClaim(claimName);
        if (authorities instanceof String) {
            if (StringUtils.hasText((String) authorities)) {
                return Arrays.asList(((String) authorities).split(" "));
            }
            return Collections.emptyList();
        }
        if (authorities instanceof Collection) {
            return castAuthoritiesToCollection(authorities);
        }
        return Collections.emptyList();
    }

    /**
     * 铸造部门收集
     *
     * @param authorities 当局
     * @return {@link Collection }<{@link String }>
     * @since 2023-07-04 09:58:04
     */
    @SuppressWarnings("unchecked")
    private Collection<String> castAuthoritiesToCollection(Object authorities) {
        return (Collection<String>) authorities;
    }
}
