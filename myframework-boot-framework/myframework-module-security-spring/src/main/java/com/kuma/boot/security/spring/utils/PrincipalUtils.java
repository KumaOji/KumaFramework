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

package com.kuma.boot.security.spring.utils;

import com.kuma.boot.security.spring.constants.BaseConstants;
import com.kuma.boot.security.spring.core.PrincipalDetails;
import com.kuma.boot.security.spring.core.userdetails.KmcUser;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;

/**
 * <p>身份信息工具类
 *
 * @author kuma
 * @version 2023.07
 * @since 2023-07-04 10:08:31
 */
public class PrincipalUtils {

    /**
     * 主要细节
     *
     * @param kmcUser 希罗多德用户
     * @return {@link PrincipalDetails }
     * @since 2023-07-04 10:08:31
     */
    public static PrincipalDetails toPrincipalDetails(KmcUser kmcUser) {
        PrincipalDetails details = new PrincipalDetails();
        //        details.setOpenId(kmcUser.getUserId());
        //        details.setUserName(kmcUser.getUsername());
        //        details.setRoles(kmcUser.getRoles());
        //        details.setAvatar(kmcUser.getAvatar());
        //        details.setEmployeeId(kmcUser.getEmployeeId());
        return details;
    }

    /**
     * 主要细节
     *
     * @param authenticatedPrincipal 经过身份验证主体
     * @return {@link PrincipalDetails }
     * @since 2023-07-04 10:08:31
     */
    public static PrincipalDetails toPrincipalDetails(
            OAuth2AuthenticatedPrincipal authenticatedPrincipal) {
        PrincipalDetails details = new PrincipalDetails();
        details.setOpenId(authenticatedPrincipal.getAttribute(BaseConstants.OPEN_ID));
        details.setUserName(authenticatedPrincipal.getName());
        List<String> roles = authenticatedPrincipal.getAttribute(BaseConstants.ROLES);
        if (CollectionUtils.isNotEmpty(roles)) {
            details.setRoles(new HashSet<>(roles));
        }
        details.setAvatar(authenticatedPrincipal.getAttribute(BaseConstants.AVATAR));
        details.setEmployeeId(authenticatedPrincipal.getAttribute(BaseConstants.EMPLOYEE_ID));
        return details;
    }

    /**
     * 主要细节
     *
     * @param jwt jwt
     * @return {@link PrincipalDetails }
     * @since 2023-07-04 10:08:31
     */
    public static PrincipalDetails toPrincipalDetails(Jwt jwt) {
        PrincipalDetails details = new PrincipalDetails();
        details.setOpenId(jwt.getClaimAsString(BaseConstants.OPEN_ID));
        details.setUserName(jwt.getClaimAsString(JwtClaimNames.SUB));
        details.setRoles(jwt.getClaim(BaseConstants.ROLES));
        details.setAvatar(jwt.getClaimAsString(BaseConstants.AVATAR));
        details.setEmployeeId(jwt.getClaimAsString(BaseConstants.EMPLOYEE_ID));
        return details;
    }
}
