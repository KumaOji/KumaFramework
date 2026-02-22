/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections4.CollectionUtils
 *  org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal
 *  org.springframework.security.oauth2.jwt.Jwt
 */
package com.kuma.boot.security.spring.utils;

import com.kuma.boot.security.spring.core.PrincipalDetails;
import com.kuma.boot.security.spring.core.userdetails.TtcUser;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

public class PrincipalUtils {
    public static PrincipalDetails toPrincipalDetails(TtcUser ttcUser) {
        PrincipalDetails details = new PrincipalDetails();
        return details;
    }

    public static PrincipalDetails toPrincipalDetails(OAuth2AuthenticatedPrincipal authenticatedPrincipal) {
        PrincipalDetails details = new PrincipalDetails();
        details.setOpenId((String)authenticatedPrincipal.getAttribute("openid"));
        details.setUserName(authenticatedPrincipal.getName());
        List roles = (List)authenticatedPrincipal.getAttribute("roles");
        if (CollectionUtils.isNotEmpty((Collection)roles)) {
            details.setRoles(new HashSet<String>(roles));
        }
        details.setAvatar((String)authenticatedPrincipal.getAttribute("avatar"));
        details.setEmployeeId((String)authenticatedPrincipal.getAttribute("employeeId"));
        return details;
    }

    public static PrincipalDetails toPrincipalDetails(Jwt jwt) {
        PrincipalDetails details = new PrincipalDetails();
        details.setOpenId(jwt.getClaimAsString("openid"));
        details.setUserName(jwt.getClaimAsString("sub"));
        details.setRoles((Set)jwt.getClaim("roles"));
        details.setAvatar(jwt.getClaimAsString("avatar"));
        details.setEmployeeId(jwt.getClaimAsString("employeeId"));
        return details;
    }
}

