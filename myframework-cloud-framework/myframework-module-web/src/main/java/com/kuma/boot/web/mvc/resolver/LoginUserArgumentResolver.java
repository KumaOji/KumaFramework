/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.model.BaseSecurityUser
 *  com.kuma.boot.security.spring.core.userdetails.KmcUser
 *  com.kuma.boot.security.spring.utils.SecurityUtils
 *  jakarta.servlet.http.HttpServletRequest
 *  org.springframework.core.MethodParameter
 *  org.springframework.web.bind.support.WebDataBinderFactory
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.method.support.HandlerMethodArgumentResolver
 *  org.springframework.web.method.support.ModelAndViewContainer
 */
package com.kuma.boot.web.mvc.resolver;

import com.kuma.boot.common.model.BaseSecurityUser;
import com.kuma.boot.security.spring.core.userdetails.KmcUser;
import com.kuma.boot.security.spring.utils.SecurityUtils;
import com.kuma.boot.web.annotation.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LoginUserArgumentResolver
implements HandlerMethodArgumentResolver {
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isHasEnableUserAnn = parameter.hasParameterAnnotation(LoginUser.class);
        boolean isHasLoginUserParameter = parameter.getParameterType().isAssignableFrom(BaseSecurityUser.class);
        return isHasEnableUserAnn && isHasLoginUserParameter;
    }

    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        LoginUser user = (LoginUser)methodParameter.getParameterAnnotation(LoginUser.class);
        boolean value = user.value();
        HttpServletRequest request = (HttpServletRequest)nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        KmcUser loginUser = SecurityUtils.getCurrentUser();
        return loginUser;
    }
}

