/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.kuma.boot.common.enums.ResultEnum
 *  com.kuma.boot.common.exception.BusinessException
 *  com.kuma.boot.common.model.Result
 *  com.kuma.boot.common.utils.common.JsonUtils
 *  com.kuma.boot.common.utils.context.ContextUtils
 *  jakarta.servlet.http.HttpServletResponse
 *  org.apache.commons.collections4.CollectionUtils
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.dromara.hutool.core.bean.BeanUtil
 *  org.dromara.hutool.core.bean.copier.CopyOptions
 *  org.dromara.hutool.core.util.CharsetUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.security.authentication.UsernamePasswordAuthenticationToken
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.core.context.SecurityContext
 *  org.springframework.security.core.context.SecurityContextHolder
 *  org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
 *  org.springframework.security.crypto.factory.PasswordEncoderFactories
 *  org.springframework.security.crypto.password.PasswordEncoder
 *  org.springframework.security.oauth2.jwt.Jwt
 *  org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
 *  org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal
 *  org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher
 *  org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher$Builder
 *  org.springframework.security.web.util.matcher.RequestMatcher
 */
package com.kuma.boot.security.spring.utils;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.Result;
import com.kuma.boot.common.utils.common.JsonUtils;
import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.security.spring.core.userdetails.TtcUser;
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
import org.apache.commons.lang3.StringUtils;
import org.dromara.hutool.core.bean.BeanUtil;
import org.dromara.hutool.core.bean.copier.CopyOptions;
import org.dromara.hutool.core.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public final class SecurityUtils {
    private static final Logger log = LoggerFactory.getLogger(SecurityUtils.class);
    private static final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    public static final String PREFIX_ROLE = "ROLE_";
    public static final String PREFIX_SCOPE = "SCOPE_";
    private static final String BASIC_ = "Basic ";

    private SecurityUtils() {
    }

    public static String encrypt(String password) {
        return passwordEncoder.encode((CharSequence)password);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches((CharSequence)rawPassword, encodedPassword);
    }

    public static SecurityContext getSecurityContext() {
        return SecurityContextHolder.getContext();
    }

    public static boolean isAuthenticated() {
        return ObjectUtils.isNotEmpty((Object)SecurityUtils.getAuthentication()) && SecurityUtils.getAuthentication().isAuthenticated();
    }

    public static Object getDetails() {
        return SecurityUtils.getAuthentication().getDetails();
    }

    public static void reloadAuthority(TtcUser newTtcUser) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken((Object)newTtcUser, (Object)newTtcUser.getPassword(), newTtcUser.getAuthorities());
        token.setDetails(SecurityUtils.getDetails());
        SecurityUtils.getSecurityContext().setAuthentication((Authentication)token);
    }

    public static TtcUser getPrincipal() {
        if (SecurityUtils.isAuthenticated()) {
            Authentication authentication = SecurityUtils.getAuthentication();
            Object object = authentication.getPrincipal();
            if (object instanceof OAuth2IntrospectionAuthenticatedPrincipal) {
                OAuth2IntrospectionAuthenticatedPrincipal introspectionPrincipal = (OAuth2IntrospectionAuthenticatedPrincipal)object;
                return new TtcUser(null, introspectionPrincipal.getUsername(), null, introspectionPrincipal.getAuthorities());
            }
            if (authentication.getPrincipal() instanceof TtcUser) {
                return (TtcUser)((Object)authentication.getPrincipal());
            }
            if (authentication.getPrincipal() instanceof Map) {
                Map principal = (Map)authentication.getPrincipal();
                return (TtcUser)((Object)BeanUtil.toBean((Object)principal, TtcUser.class, (CopyOptions)new CopyOptions()));
            }
        }
        return null;
    }

    public static TtcUser getPrincipals() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal != null) {
            if (principal instanceof TtcUser) {
                return (TtcUser)((Object)principal);
            }
            if (principal instanceof LinkedHashMap) {
                return null;
            }
            if (principal instanceof String && principal.equals("anonymousUser")) {
                return null;
            }
            throw new IllegalStateException("\u83b7\u53d6\u7528\u6237\u6570\u636e\u5931\u8d25");
        }
        return null;
    }

    public static String[] whitelistToAntMatchers(List<String> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            String[] array = new String[list.size()];
            log.info("Fetch The REST White List.");
            return list.toArray(array);
        }
        log.error("Can not Fetch The REST White List Configurations.");
        return new String[0];
    }

    public static String wellFormRolePrefix(String content) {
        return SecurityUtils.wellFormPrefix(content, PREFIX_ROLE);
    }

    public static String wellFormPrefix(String content, String prefix) {
        if (StringUtils.startsWith((CharSequence)content, (CharSequence)prefix)) {
            return content;
        }
        return prefix + content;
    }

    public static void writeResponse(Result<?> result, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(CharsetUtil.UTF_8.name());
        response.setContentType("application/json");
        PrintWriter printWriter = response.getWriter();
        printWriter.write(JsonUtils.toJSONString(result));
        printWriter.flush();
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static TtcUser getUser(Authentication authentication) {
        if (Objects.isNull(authentication)) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (Objects.isNull(principal)) {
            return null;
        }
        if (principal instanceof TtcUser) {
            return (TtcUser)((Object)principal);
        }
        if (principal instanceof Map) {
            return (TtcUser)((Object)JsonUtils.toObject((String)JsonUtils.toJSONString((Object)principal), TtcUser.class));
        }
        return null;
    }

    public static TtcUser getCurrentUser() {
        TtcUser securityUser = SecurityUtils.getUser(SecurityUtils.getAuthentication());
        if (Objects.isNull((Object)securityUser)) {
            throw new BusinessException(ResultEnum.USER_NOT_LOGIN);
        }
        return securityUser;
    }

    public static TtcUser getCurrentUserWithNull() {
        Authentication authentication = SecurityUtils.getAuthentication();
        return SecurityUtils.getUser(authentication);
    }

    public static String getUsername() {
        return SecurityUtils.getCurrentUser().getUsername();
    }

    public static String getUsernameWithAnonymous() {
        return SecurityUtils.getCurrentUserWithNull() == null ? "anonymous" : SecurityUtils.getCurrentUserWithNull().getUsername();
    }

    public static Long getUserIdWithAnonymous() {
        return SecurityUtils.getCurrentUserWithNull() == null ? -1L : SecurityUtils.getCurrentUserWithNull().getUserId();
    }

    public static Long getUserId() {
        return SecurityUtils.getCurrentUser().getUserId();
    }

    public static boolean validatePass(String newPass, String passwordEncoderOldPass) {
        return SecurityUtils.getPasswordEncoder().matches((CharSequence)newPass, passwordEncoderOldPass);
    }

    public static BCryptPasswordEncoder getPasswordEncoder() {
        BCryptPasswordEncoder passwordEncoder = (BCryptPasswordEncoder)ContextUtils.getBean(BCryptPasswordEncoder.class, (boolean)true);
        if (Objects.isNull(passwordEncoder)) {
            passwordEncoder = new BCryptPasswordEncoder();
        }
        return passwordEncoder;
    }

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

    public static String getUsername(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String username = null;
        if (principal instanceof TtcUser) {
            username = ((TtcUser)((Object)principal)).getUsername();
        } else if (principal instanceof String) {
            username = (String)principal;
        }
        return username;
    }

    public static String getTenant() {
        return "";
    }

    public static String getClientId() {
        return "";
    }

    public static Integer userId() {
        return Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    public static List<String> authorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken)authentication;
            Jwt principal = (Jwt)jwtAuthenticationToken.getPrincipal();
            return Arrays.asList(((String)principal.getClaims().get("scp")).split(" "));
        }
        return new ArrayList<String>();
    }

    public static List<String> roles() {
        return Objects.requireNonNull(SecurityUtils.authorities()).stream().filter(authority -> authority.startsWith(PREFIX_ROLE)).toList();
    }

    public static RequestMatcher[] toRequestMatchers(List<String> paths) {
        if (CollectionUtils.isNotEmpty(paths)) {
            List<PathPatternRequestMatcher> matchers = paths.stream().map(arg_0 -> ((PathPatternRequestMatcher.Builder)PathPatternRequestMatcher.withDefaults()).matcher(arg_0)).toList();
            RequestMatcher[] result = new RequestMatcher[matchers.size()];
            return matchers.toArray(result);
        }
        return new RequestMatcher[0];
    }
}

