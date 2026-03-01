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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.CharsetUtil;
import com.kuma.boot.common.model.result.Result;
import com.kuma.boot.common.utils.json.JacksonUtils;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.security.spring.core.userdetails.KmcUser;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * SecurityUtil
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 14:55:47
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    /**
     * 日志
     */
    private static final Logger log = LoggerFactory.getLogger(SecurityUtils.class);

    /**
     * 密码编码器
     */
    private static final PasswordEncoder passwordEncoder =
            PasswordEncoderFactories.createDelegatingPasswordEncoder();

    /**
     * 前缀作用
     */
    public static final String PREFIX_ROLE = "ROLE_";

    /**
     * 前缀范围
     */
    public static final String PREFIX_SCOPE = "SCOPE_";

    /**
     * 密码加密
     *
     * @param password 明文密码
     * @return {@link String }
     * @since 2023-07-04 10:08:40
     */
    public static String encrypt(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * 密码验证
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 加密后的密码
     * @return boolean
     * @since 2023-07-04 10:08:40
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 得到安全上下文
     *
     * @return {@link SecurityContext }
     * @since 2023-07-04 10:08:40
     */
    public static SecurityContext getSecurityContext() {
        return SecurityContextHolder.getContext();
    }

    /**
     * 身份验证
     *
     * @return boolean
     * @since 2023-07-04 10:08:40
     */
    public static boolean isAuthenticated() {
        return ObjectUtils.isNotEmpty(getAuthentication()) && getAuthentication().isAuthenticated();
    }

    /**
     * 获得详细信息
     *
     * @return {@link Object }
     * @since 2023-07-04 10:08:40
     */
    public static Object getDetails() {
        return getAuthentication().getDetails();
    }

    /**
     * 当用户角色发生变化，或者用户角色对应的权限发生变化，那么就从数据库中重新查询用户相关信息
     *
     * @param newKmcUser 从数据库中重新查询并生成的用户信息
     * @since 2023-07-04 10:08:40
     */
    public static void reloadAuthority(KmcUser newKmcUser) {
        // 重新new一个token，因为Authentication中的权限是不可变的.
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        newKmcUser, newKmcUser.getPassword(), newKmcUser.getAuthorities());
        token.setDetails(getDetails());
        getSecurityContext().setAuthentication(token);
    }

    /**
     * 获取认证用户信息
     * <p>
     * 该方法仅能获取有限用户信息。从实用角度建议使用本系统提供的其它获取用户方式。
     *
     * @return {@link KmcUser }
     * @since 2023-07-04 10:08:40
     */
    public static KmcUser getPrincipal() {
        if (isAuthenticated()) {
            Authentication authentication = getAuthentication();
            if (authentication.getPrincipal()
                    instanceof OAuth2IntrospectionAuthenticatedPrincipal introspectionPrincipal) {
                return new KmcUser(
                        null,
                        introspectionPrincipal.getUsername(),
                        null,
                        introspectionPrincipal.getAuthorities());
            }
            if (authentication.getPrincipal() instanceof KmcUser) {
                return (KmcUser) authentication.getPrincipal();
            }
            if (authentication.getPrincipal() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> principal = (Map<String, Object>) authentication.getPrincipal();
                return BeanUtil.toBean(principal, KmcUser.class, new CopyOptions());
            }
        }

        return null;
    }

    /**
     * 获得校长
     *
     * @return {@link KmcUser }
     * @since 2023-07-04 10:08:41
     */
    public static KmcUser getPrincipals() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal != null) {
            if (principal instanceof KmcUser) {
                return (KmcUser) principal;
            } else if (principal instanceof LinkedHashMap) {
                // TODO: zhangyu 2019/7/15 感觉还可以升级一把，不吐linkedhashmap 直接就是oauth2user
                // 2019/7/20 试验过将OAuth2UserAuthenticationConverter
                // map<string,?>中的?强制转换成oauth2user，试验失败，问题不是很急，可以先放着
                /**
                 * https://blog.csdn.net/m0_37834471/article/details/81814233
                 * cn/itcraftsman/luban/auth/oauth2/OAuth2UserAuthenticationConverter.java
                 */
                //                KmcUser user = new KmcUser();
                //                BeanUtil.fillBeanWithMap((LinkedHashMap) principal, user, true);
                return null;
            } else if (principal instanceof String && principal.equals("anonymousUser")) {
                return null;
            } else {
                throw new IllegalStateException("获取用户数据失败");
            }
        }
        return null;
    }

    /**
     * 白名单,蚂蚁匹配器
     *
     * @param list 列表
     * @return {@link String[] }
     * @since 2023-07-04 10:08:41
     */
    public static String[] whitelistToAntMatchers(List<String> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            String[] array = new String[list.size()];
            log.info("Fetch The REST White List.");
            return list.toArray(array);
        }

        log.error("Can not Fetch The REST White List Configurations.");
        return new String[] {};
    }

    /**
     * 好前缀形式作用
     *
     * @param content 内容
     * @return {@link String }
     * @since 2023-07-04 10:08:41
     */
    public static String wellFormRolePrefix(String content) {
        return wellFormPrefix(content, PREFIX_ROLE);
    }

    /**
     * 好前缀形式
     *
     * @param content 内容
     * @param prefix  前缀
     * @return {@link String }
     * @since 2023-07-04 10:08:41
     */
    public static String wellFormPrefix(String content, String prefix) {
        if (StringUtils.startWith(content, prefix)) {
            return content;
        } else {
            return prefix + content;
        }
    }

    /**
     * Basic
     */
    private static final String BASIC_ = "Basic ";

    /**
     * 回写数据
     *
     * @param result   result
     * @param response response
     * @since 2021-09-02 14:55:57
     */
    public static void writeResponse(Result<?> result, HttpServletResponse response)
            throws IOException {
        response.setCharacterEncoding(CharsetUtil.UTF_8);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter printWriter = response.getWriter();
        printWriter.write(JacksonUtils.toJSONString(result));
        printWriter.flush();
    }

    /**
     * 获取认证信息
     *
     * @return 认证信息
     * @since 2021-09-02 14:56:05
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取用户信息
     *
     * @param authentication 认证信息
     * @return 用户信息
     * @since 2021-09-02 14:56:13
     */
    public static KmcUser getUser(Authentication authentication) {
        if (Objects.isNull(authentication)) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (Objects.isNull(principal)) {
            return null;
        }

        if (principal instanceof KmcUser) {
            return (KmcUser) principal;
        } else if (principal instanceof Map) {
            return JacksonUtils.toObject(JacksonUtils.toJSONString(principal), KmcUser.class);
        }

        return null;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     * @since 2021-09-02 14:56:28
     */
    public static KmcUser getCurrentUser() {
        KmcUser securityUser = getUser(getAuthentication());
        if (Objects.isNull(securityUser)) {
            //todo 需要修改
//            throw new BusinessException(ResultEnum.USER_NOT_LOGIN);
        }
        return securityUser;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     * @since 2021-09-02 14:56:28
     */
    public static KmcUser getCurrentUserWithNull() {
        Authentication authentication = getAuthentication();
        return getUser(authentication);
    }

    /**
     * 获取用户姓名
     *
     * @return 用户姓名
     * @since 2021-09-02 14:56:33
     */
    public static String getUsername() {
        return getCurrentUser().getUsername();
    }

    public static String getUsernameWithAnonymous() {
        return getCurrentUserWithNull() == null
                ? "anonymous"
                : getCurrentUserWithNull().getUsername();
    }

    public static Long getUserIdWithAnonymous() {
        return getCurrentUserWithNull() == null ? -1L : getCurrentUserWithNull().getUserId();
    }

    /**
     * 获取用户id
     *
     * @return 用户id
     * @since 2021-09-02 14:56:38
     */
    public static Long getUserId() {
        return getCurrentUser().getUserId();
    }

    /// **
    // * 获取客户端id
    // *
    // * @return java.lang.String
    // * @author kuma
    // * @since 2020/10/15 15:55
    // */
    // public String getClientId() {
    //	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //	if (authentication instanceof OAuth2Authentication) {
    //		OAuth2Authentication auth2Authentication = (OAuth2Authentication) authentication;
    //		return auth2Authentication.getOAuth2Request().getClientId();
    //	}
    //	return null;
    // }

    /// **
    // * 获取request(header/param)中的token
    // *
    // * @param request request
    // * @return java.lang.String
    // * @author kuma
    // * @since 2021/2/25 16:58
    // */
    // public String extractToken(HttpServletRequest request) {
    //	String token = extractHeaderToken(request);
    //	if (token == null) {
    //		token = request.getParameter(OAuth2AccessToken.ACCESS_TOKEN);
    //		if (token == null) {
    //			LogUtil.error("Token not found in request parameters.  Not an OAuth2 request.");
    //		}
    //	}
    //	return token;
    // }

    /**
     * 验证密码
     *
     * @param newPass                密码
     * @param passwordEncoderOldPass 加密后的密码
     * @return 是否成功
     * @since 2021-09-02 14:57:20
     */
    public static boolean validatePass(String newPass, String passwordEncoderOldPass) {
        return getPasswordEncoder().matches(newPass, passwordEncoderOldPass);
    }

    /**
     * 获取密码加密工具
     *
     * @return 加密对象
     * @since 2021-09-02 14:57:28
     */
    public static BCryptPasswordEncoder getPasswordEncoder() {
        BCryptPasswordEncoder passwordEncoder =
                ContextUtils.getBean(BCryptPasswordEncoder.class, true);
        if (Objects.isNull(passwordEncoder)) {
            passwordEncoder = new BCryptPasswordEncoder();
        }
        return passwordEncoder;
    }

    /// **
    // * 解析head中的token
    // *
    // * @param request request
    // * @return java.lang.String
    // * @author kuma
    // * @since 2021/2/25 16:59
    // */
    // private String extractHeaderToken(HttpServletRequest request) {
    //	Enumeration<String> headers = request.getHeaders(CommonConstant.TOKEN_HEADER);
    //	while (headers.hasMoreElements()) {
    //		String value = headers.nextElement();
    //		if ((value.startsWith(OAuth2AccessToken.BEARER_TYPE))) {
    //			String authHeaderValue = value.substring(OAuth2AccessToken.BEARER_TYPE.length())
    //				.trim();
    //			int commaIndex = authHeaderValue.indexOf(',');
    //			if (commaIndex > 0) {
    //				authHeaderValue = authHeaderValue.substring(0, commaIndex);
    //			}
    //			return authHeaderValue;
    //		}
    //	}
    //	return null;
    // }

    /// **
    // * 从header 请求中的clientId:clientSecret
    // *
    // * @param request request
    // * @return java.lang.String[]
    // * @author kuma
    // * @since 2021/2/25 16:59
    // */
    // public String[] extractClient(HttpServletRequest request) {
    //	String header = request.getHeader("BasicAuthorization");
    //	if (header == null || !header.startsWith(BASIC_)) {
    //		throw new UnapprovedClientAuthenticationException("请求头中client信息为空");
    //	}
    //	return extractHeaderClient(header);
    // }

    /**
     * 从header 请求中的clientId:clientSecret
     *
     * @param header header
     * @return header参数列表
     * @since 2021-09-02 14:57:55
     */
    public static String[] extractHeaderClient(String header) {
        byte[] base64Client = header.substring(BASIC_.length()).getBytes(StandardCharsets.UTF_8);
        byte[] decoded = Base64.getDecoder().decode(base64Client);
        String clientStr = new String(decoded, StandardCharsets.UTF_8);
        String[] clientArr = clientStr.split(":");
        if (clientArr.length != 2) {
            throw new RuntimeException("Invalid basic authentication token");
        }
        return clientArr;
    }

    /**
     * 获取登陆的用户名
     *
     * @param authentication 认证信息
     * @return 用户名
     * @since 2021-09-02 14:58:07
     */
    public static String getUsername(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String username = null;
        if (principal instanceof KmcUser) {
            username = ((KmcUser) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        }
        return username;
    }

    /**
     * 获取租户信息
     *
     * @return 租户信息
     * @since 2021-09-02 14:58:20
     */
    public static String getTenant() {
        // todo
        return "";
    }

    /**
     * getClientId
     *
     * @return ClientId
     * @since 2021-09-02 14:58:29
     */
    public static String getClientId() {
        // todo
        return "";
    }

    public static Integer userId() {
        return Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    public static List<String> authorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            Jwt principal = (Jwt) jwtAuthenticationToken.getPrincipal();
            return Arrays.asList(((String) principal.getClaims().get("scp")).split(" "));
        }
        return new ArrayList<>();
    }

    public static List<String> roles() {
        return Objects.requireNonNull(SecurityUtils.authorities()).stream()
                .filter(authority -> authority.startsWith("ROLE_"))
                .toList();
    }

    public static RequestMatcher[] toRequestMatchers(List<String> paths) {
        if (CollectionUtils.isNotEmpty(paths)) {
            List<PathPatternRequestMatcher> matchers =
                    paths.stream().map(PathPatternRequestMatcher.withDefaults()::matcher).toList();
            RequestMatcher[] result = new RequestMatcher[matchers.size()];
            return matchers.toArray(result);
        } else {
            return new RequestMatcher[] {};
        }
    }
}
