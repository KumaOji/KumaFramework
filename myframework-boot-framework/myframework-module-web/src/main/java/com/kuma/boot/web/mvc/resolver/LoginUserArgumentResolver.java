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

package com.kuma.boot.web.mvc.resolver;

import com.kuma.boot.common.model.BaseSecurityUser;
import com.kuma.boot.security.spring.utils.SecurityUtils;
import com.kuma.boot.web.annotation.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 通过header里的token获取用户信息
 *
 * @author kuma
 * @version 2021.9
 * @see <a
 * href="https://my.oschina.net/u/4149877/blog/3143391/print">https://my.oschina.net/u/4149877/blog/3143391/print</a>
 * @see <a
 * href="https://blog.csdn.net/aiyaya_/article/details/79221733">https://blog.csdn.net/aiyaya_/article/details/79221733</a>
 * @since 2021-09-02 21:32:45
 */
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    public LoginUserArgumentResolver() {}

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isHasEnableUserAnn = parameter.hasParameterAnnotation(LoginUser.class);
        boolean isHasLoginUserParameter =
                parameter.getParameterType().isAssignableFrom(BaseSecurityUser.class);
        return isHasEnableUserAnn && isHasLoginUserParameter;
    }

    @Override
    public Object resolveArgument(
            MethodParameter methodParameter,
            ModelAndViewContainer modelAndViewContainer,
            NativeWebRequest nativeWebRequest,
            WebDataBinderFactory webDataBinderFactory)
            throws Exception {
        LoginUser user = methodParameter.getParameterAnnotation(LoginUser.class);
        boolean value = user.value();
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        BaseSecurityUser loginUser = SecurityUtils.getCurrentUser();

        // 根据value状态获取更多用户信息，待实现
        return loginUser;
    }
}
